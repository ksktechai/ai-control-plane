package com.ai.rag;

import com.ai.domain.Chunk;
import com.ai.domain.Embedding;
import com.pgvector.PGvector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC implementation of ChunkRepository using pgvector.
 */
@Repository
public class JdbcChunkRepository implements ChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Chunk save(Chunk chunk) {
        String sql = "INSERT INTO chunks (id, document_id, text, position, embedding) " +
                    "VALUES (?, ?, ?, ?, ?::vector) " +
                    "ON CONFLICT (id) DO UPDATE SET " +
                    "text = EXCLUDED.text, embedding = EXCLUDED.embedding";

        PGvector pgVector = new PGvector(chunk.embedding().vector());

        jdbcTemplate.update(sql,
            chunk.id(),
            chunk.documentId(),
            chunk.text(),
            chunk.position(),
            pgVector.toString()
        );

        return chunk;
    }

    @Override
    public List<Chunk> findSimilar(float[] queryEmbedding, int topK) {
        String sql = "SELECT id, document_id, text, position, embedding " +
                    "FROM chunks " +
                    "ORDER BY embedding <=> ?::vector " +
                    "LIMIT ?";

        PGvector pgVector = new PGvector(queryEmbedding);

        return jdbcTemplate.query(sql, new ChunkRowMapper(), pgVector.toString(), topK);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM chunks");
    }

    private static class ChunkRowMapper implements RowMapper<Chunk> {
        @Override
        public Chunk mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String documentId = rs.getString("document_id");
            String text = rs.getString("text");
            int position = rs.getInt("position");

            Object embeddingObj = rs.getObject("embedding");
            float[] vector;
            if (embeddingObj instanceof PGvector pgVector) {
                vector = pgVector.toArray();
            } else if (embeddingObj instanceof org.postgresql.util.PGobject pgObject) {
                // Parse the PGobject string representation as a PGvector
                vector = new PGvector(pgObject.getValue()).toArray();
            } else {
                throw new SQLException("Unexpected type for embedding column: " +
                    (embeddingObj != null ? embeddingObj.getClass().getName() : "null"));
            }
            Embedding embedding = new Embedding(vector, "nomic-embed-text");

            return new Chunk(id, documentId, text, position, embedding);
        }
    }
}
