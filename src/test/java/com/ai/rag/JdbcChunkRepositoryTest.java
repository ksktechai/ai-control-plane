package com.ai.rag;

import com.ai.common.domain.Chunk;
import com.ai.common.domain.Embedding;
import com.pgvector.PGvector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcChunkRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private JdbcChunkRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcChunkRepository(jdbcTemplate);
    }

    @Test
    void shouldSaveChunk() {
        Embedding embedding = new Embedding(new float[]{0.1f, 0.2f, 0.3f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "sample text", 0, embedding);

        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any()))
            .thenReturn(1);

        Chunk result = repository.save(chunk);

        assertThat(result).isEqualTo(chunk);
        verify(jdbcTemplate).update(
            contains("INSERT INTO chunks"),
            eq("chunk-1"),
            eq("doc-1"),
            eq("sample text"),
            eq(0),
            anyString()
        );
    }

    @Test
    void shouldFindSimilarChunks() {
        float[] queryEmbedding = {0.1f, 0.2f, 0.3f};
        Embedding embedding = new Embedding(queryEmbedding, "nomic-embed-text");
        Chunk chunk1 = new Chunk("chunk-1", "doc-1", "text1", 0, embedding);
        Chunk chunk2 = new Chunk("chunk-2", "doc-2", "text2", 1, embedding);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt()))
            .thenReturn(List.of(chunk1, chunk2));

        List<Chunk> results = repository.findSimilar(queryEmbedding, 5);

        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(chunk1, chunk2);
        verify(jdbcTemplate).query(
            contains("ORDER BY embedding <=> ?::vector"),
            any(RowMapper.class),
            anyString(),
            eq(5)
        );
    }

    @Test
    void shouldDeleteAll() {
        when(jdbcTemplate.update(anyString())).thenReturn(10);

        repository.deleteAll();

        verify(jdbcTemplate).update("DELETE FROM chunks");
    }

    @Test
    void shouldMapRowToChunk() throws SQLException {
        // Create a mock ResultSet
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("chunk-1");
        when(rs.getString("document_id")).thenReturn("doc-1");
        when(rs.getString("text")).thenReturn("sample text");
        when(rs.getInt("position")).thenReturn(0);

        float[] vector = {0.1f, 0.2f, 0.3f};
        PGvector pgVector = new PGvector(vector);
        when(rs.getObject("embedding")).thenReturn(pgVector);

        // Use reflection to access the private ChunkRowMapper
        // We can test this indirectly through findSimilar, or we can create the mapper
        // For simplicity, let's test through findSimilar which uses the mapper

        Embedding embedding = new Embedding(vector, "nomic-embed-text");
        Chunk expectedChunk = new Chunk("chunk-1", "doc-1", "sample text", 0, embedding);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt()))
            .thenAnswer(invocation -> {
                RowMapper<Chunk> mapper = invocation.getArgument(1);
                return List.of(mapper.mapRow(rs, 0));
            });

        List<Chunk> results = repository.findSimilar(vector, 1);

        assertThat(results).hasSize(1);
        Chunk result = results.get(0);
        assertThat(result.id()).isEqualTo("chunk-1");
        assertThat(result.documentId()).isEqualTo("doc-1");
        assertThat(result.text()).isEqualTo("sample text");
        assertThat(result.position()).isEqualTo(0);
        assertThat(result.embedding().vector()).isEqualTo(vector);
    }
}
