package com.ai.control;

import com.ai.common.domain.AnswerResult;
import com.ai.common.domain.Question;

/**
 * Main control plane interface - the "brain" of the system.
 * Coordinates retrieval, generation, and verification with intelligent retry logic.
 */
public interface ControlPlane {

    /**
     * Answers a question using RAG with automatic grounding verification and retry.
     *
     * @param question The question to answer
     * @return The answer result with verification and confidence
     */
    AnswerResult answer(Question question);
}
