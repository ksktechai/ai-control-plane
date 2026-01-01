package com.ai.embeddings;

import com.ai.domain.Embedding;

/**
 * Service for generating text embeddings.
 */
public interface EmbeddingService {

    /**
     * Generates an embedding vector for the given text.
     *
     * @param text The text to embed
     * @return The embedding vector
     */
    Embedding generateEmbedding(String text);

    /**
     * Returns the dimension of embeddings produced by this service.
     *
     * @return The embedding dimension
     */
    int getDimension();
}
