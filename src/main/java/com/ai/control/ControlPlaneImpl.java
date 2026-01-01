package com.ai.control;

import com.ai.domain.*;
import com.ai.model.LlmModel;
import com.ai.model.RetrievalStrategy;
import com.ai.util.CorrelationIdHolder;
import com.ai.llm.OllamaClient;
import com.ai.rag.RetrievalService;
import com.ai.verifier.AnswerVerifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the control plane with intelligent model selection and retry logic.
 */
@Service
public class ControlPlaneImpl implements ControlPlane {
    private static final Logger logger = LogManager.getLogger(ControlPlaneImpl.class);
    private static final int MAX_RETRIES = 2;
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.7;

    private final RetrievalService retrievalService;
    private final OllamaClient ollamaClient;
    private final AnswerVerifier answerVerifier;

    public ControlPlaneImpl(RetrievalService retrievalService,
                           OllamaClient ollamaClient,
                           AnswerVerifier answerVerifier) {
        this.retrievalService = retrievalService;
        this.ollamaClient = ollamaClient;
        this.answerVerifier = answerVerifier;
    }

    @Override
    public AnswerResult answer(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        CorrelationIdHolder.set(question.correlationId());
        logger.info("ControlPlane processing question - correlationId: {}", question.correlationId());

        // Start with small model and simple retrieval
        LlmModel currentModel = LlmModel.PHI_3_MINI;
        RetrievalStrategy currentStrategy = RetrievalStrategy.SIMPLE;
        int attemptNumber = 0;

        while (attemptNumber < MAX_RETRIES) {
            attemptNumber++;
            logger.info("Attempt {}/{} - model: {}, strategy: {}",
                attemptNumber, MAX_RETRIES, currentModel, currentStrategy);

            try {
                // Retrieve context
                RetrievalResult retrievalResult = retrievalService.retrieve(
                    question.text(),
                    currentStrategy
                );

                // Generate answer
                Answer answer = generateAnswer(question.text(), retrievalResult, currentModel);

                // Verify grounding
                VerificationResult verification = answerVerifier.verify(answer, retrievalResult);

                double confidence = calculateConfidence(verification);

                logger.info("Attempt {} completed - verification: {}, confidence: {:.2f}",
                    attemptNumber, verification.status(), confidence);

                // Check if answer meets quality threshold
                if (confidence >= MIN_CONFIDENCE_THRESHOLD ||
                    attemptNumber >= MAX_RETRIES) {
                    return new AnswerResult(answer, verification, confidence,
                        currentStrategy.name());
                }

                // Escalate for next attempt
                currentModel = escalateModel(currentModel);
                currentStrategy = escalateStrategy(currentStrategy);

                logger.info("Confidence below threshold, escalating - newModel: {}, newStrategy: {}",
                    currentModel, currentStrategy);

            } catch (Exception e) {
                logger.error("Attempt {} failed: {}", attemptNumber, e.getMessage(), e);

                if (attemptNumber >= MAX_RETRIES) {
                    throw new ControlPlaneException("Failed to generate answer after "
                        + MAX_RETRIES + " attempts", e);
                }

                // Escalate and retry
                currentModel = escalateModel(currentModel);
                currentStrategy = escalateStrategy(currentStrategy);
            }
        }

        throw new ControlPlaneException("Failed to generate confident answer");
    }

    private Answer generateAnswer(String questionText, RetrievalResult retrievalResult,
                                  LlmModel model) {
        String context = retrievalResult.chunks().stream()
            .map(Chunk::text)
            .collect(Collectors.joining("\n\n"));

        String prompt = String.format(
            "Answer the question based only on the context provided. " +
            "If the context doesn't contain enough information, say so.\n\n" +
            "Context:\n%s\n\n" +
            "Question: %s\n\n" +
            "Answer:",
            context,
            questionText
        );

        int maxTokens = calculateMaxTokens(model);
        String responseText = ollamaClient.generate(model, prompt, maxTokens);

        List<Citation> citations = retrievalResult.chunks().stream()
            .limit(3)
            .map(chunk -> new Citation(
                chunk.id(),
                chunk.documentId(),
                chunk.text(),
                0.9  // Simplified - would calculate actual relevance
            ))
            .collect(Collectors.toList());

        return new Answer(responseText, citations, model.ollamaName());
    }

    private double calculateConfidence(VerificationResult verification) {
        return switch (verification.status()) {
            case GROUNDED -> 0.95;
            case PARTIALLY_GROUNDED -> verification.groundingScore() * 0.8;
            case UNGROUNDED -> 0.3;
            case FAILED -> 0.1;
        };
    }

    private LlmModel escalateModel(LlmModel current) {
        return switch (current) {
            case PHI_3_MINI, DEEPSEEK_R1_1_5B, LLAMA_3_2_3B -> LlmModel.QWEN_2_5_7B;
            case QWEN_2_5_7B, MISTRAL_7B -> LlmModel.LLAMA_3_1_8B;
            default -> LlmModel.QWEN_2_5_14B;
        };
    }

    private RetrievalStrategy escalateStrategy(RetrievalStrategy current) {
        return switch (current) {
            case SIMPLE -> RetrievalStrategy.DEEP;
            case DEEP -> RetrievalStrategy.EXHAUSTIVE;
            case EXHAUSTIVE -> RetrievalStrategy.EXHAUSTIVE;
        };
    }

    private int calculateMaxTokens(LlmModel model) {
        return model.isSmall() ? 256 : model.isMedium() ? 512 : 1024;
    }
}
