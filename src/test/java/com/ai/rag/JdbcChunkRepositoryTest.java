package com.ai.rag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ai.domain.Chunk;
import com.ai.domain.Embedding;
import com.pgvector.PGvector;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class JdbcChunkRepositoryTest {

    @Mock private JdbcTemplate jdbcTemplate;

    private JdbcChunkRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcChunkRepository(jdbcTemplate);
    }

    @Test
    void shouldSaveChunk() {
        Embedding embedding = new Embedding(new float[] {0.1f, 0.2f, 0.3f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "sample text", 0, embedding);

        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any())).thenReturn(1);

        Chunk result = repository.save(chunk);

        assertThat(result).isEqualTo(chunk);
        verify(jdbcTemplate)
                .update(
                        contains("INSERT INTO chunks"),
                        eq("chunk-1"),
                        eq("doc-1"),
                        eq("sample text"),
                        eq(0),
                        anyString());
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
        verify(jdbcTemplate)
                .query(
                        contains("ORDER BY embedding <=> ?::vector"),
                        any(RowMapper.class),
                        anyString(),
                        eq(5));
    }

    @Test
    void shouldDeleteAll() {
        when(jdbcTemplate.update(anyString())).thenReturn(10);

        repository.deleteAll();

        verify(jdbcTemplate).update("DELETE FROM chunks");
    }

    @Test
    void shouldMapRowToChunkWithPGvector() throws SQLException {
        // Create a mock ResultSet
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("chunk-1");
        when(rs.getString("document_id")).thenReturn("doc-1");
        when(rs.getString("text")).thenReturn("sample text");
        when(rs.getInt("position")).thenReturn(0);

        float[] vector = {0.1f, 0.2f, 0.3f};
        PGvector pgVector = new PGvector(vector);
        when(rs.getObject("embedding")).thenReturn(pgVector);

        Embedding embedding = new Embedding(vector, "nomic-embed-text");
        Chunk expectedChunk = new Chunk("chunk-1", "doc-1", "sample text", 0, embedding);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt()))
                .thenAnswer(
                        invocation -> {
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

    @Test
    void shouldMapRowToChunkWithPGobject() throws SQLException {
        // Create a mock ResultSet
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("chunk-2");
        when(rs.getString("document_id")).thenReturn("doc-2");
        when(rs.getString("text")).thenReturn("another text");
        when(rs.getInt("position")).thenReturn(1);

        // Simulate PGobject returned instead of PGvector
        float[] vector = {0.4f, 0.5f, 0.6f};
        PGobject pgObject = new PGobject();
        pgObject.setType("vector");
        pgObject.setValue("[0.4,0.5,0.6]");
        when(rs.getObject("embedding")).thenReturn(pgObject);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt()))
                .thenAnswer(
                        invocation -> {
                            RowMapper<Chunk> mapper = invocation.getArgument(1);
                            return List.of(mapper.mapRow(rs, 0));
                        });

        List<Chunk> results = repository.findSimilar(vector, 1);

        assertThat(results).hasSize(1);
        Chunk result = results.get(0);
        assertThat(result.id()).isEqualTo("chunk-2");
        assertThat(result.documentId()).isEqualTo("doc-2");
        assertThat(result.text()).isEqualTo("another text");
        assertThat(result.position()).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionForNullEmbedding() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("chunk-3");
        when(rs.getString("document_id")).thenReturn("doc-3");
        when(rs.getString("text")).thenReturn("text");
        when(rs.getInt("position")).thenReturn(0);
        when(rs.getObject("embedding")).thenReturn(null);

        float[] vector = {0.1f, 0.2f, 0.3f};

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt()))
                .thenAnswer(
                        invocation -> {
                            RowMapper<Chunk> mapper = invocation.getArgument(1);
                            return List.of(mapper.mapRow(rs, 0));
                        });

        assertThatThrownBy(() -> repository.findSimilar(vector, 1))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Unexpected type for embedding column: null");
    }

    @Test
    void shouldThrowExceptionForUnexpectedEmbeddingType() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("chunk-4");
        when(rs.getString("document_id")).thenReturn("doc-4");
        when(rs.getString("text")).thenReturn("text");
        when(rs.getInt("position")).thenReturn(0);
        when(rs.getObject("embedding")).thenReturn("unexpected string type");

        float[] vector = {0.1f, 0.2f, 0.3f};

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt()))
                .thenAnswer(
                        invocation -> {
                            RowMapper<Chunk> mapper = invocation.getArgument(1);
                            return List.of(mapper.mapRow(rs, 0));
                        });

        assertThatThrownBy(() -> repository.findSimilar(vector, 1))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Unexpected type for embedding column: java.lang.String");
    }

    @Test
    void shouldFindEmptyResults() {
        float[] queryEmbedding = {0.1f, 0.2f, 0.3f};

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt()))
                .thenReturn(List.of());

        List<Chunk> results = repository.findSimilar(queryEmbedding, 5);

        assertThat(results).isEmpty();
    }
}
