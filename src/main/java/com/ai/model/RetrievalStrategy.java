package com.ai.common.model;

/**
 * Strategies for retrieving context from the knowledge base.
 */
public enum RetrievalStrategy {
    /**
     * Simple top-K semantic search.
     */
    SIMPLE(5),

    /**
     * Increased retrieval depth for complex questions.
     */
    DEEP(10),

    /**
     * Maximum context retrieval for difficult questions.
     */
    EXHAUSTIVE(20);

    private final int defaultTopK;

    RetrievalStrategy(int defaultTopK) {
        this.defaultTopK = defaultTopK;
    }

    public int defaultTopK() {
        return defaultTopK;
    }
}
