package com.ai.verifier;

import com.ai.common.domain.Answer;
import com.ai.common.domain.RetrievalResult;
import com.ai.common.domain.VerificationResult;

/**
 * Service for verifying answer grounding against retrieved context.
 */
public interface AnswerVerifier {

    /**
     * Verifies that an answer is grounded in the retrieved context.
     *
     * @param answer The answer to verify
     * @param context The retrieved context
     * @return The verification result
     */
    VerificationResult verify(Answer answer, RetrievalResult context);
}
