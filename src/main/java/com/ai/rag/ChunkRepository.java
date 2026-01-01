package com.ai.rag;

import com.ai.common.domain.Chunk;

import java.util.List;

/**
 * Repository for chunk storage and retrieval.
 */
public interface ChunkRepository {

    /**
     * Saves a chunk to the repository.
     *
     * @param chunk The chunk to save
     * @return The saved chunk
     */
    Chunk save(Chunk chunk);

    /**
     * Finds similar chunks using vector similarity search.
     *
     * @param queryEmbedding The query embedding vector
     * @param topK Number of results to return
     * @return List of similar chunks ordered by similarity
     */
    List<Chunk> findSimilar(float[] queryEmbedding, int topK);

    /**
     * Deletes all chunks (for testing).
     */
    void deleteAll();
}
