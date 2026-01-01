package com.ai.rag;

import com.ai.domain.RetrievalResult;
import com.ai.model.RetrievalStrategy;

/** Service for retrieving relevant context for questions. */
public interface RetrievalService {

    /**
     * Retrieves relevant chunks for a question.
     *
     * @param question The question to retrieve context for
     * @param strategy The retrieval strategy to use
     * @return The retrieval result with relevant chunks
     */
    RetrievalResult retrieve(String question, RetrievalStrategy strategy);
}
