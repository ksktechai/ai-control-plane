package com.ai.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents the complete result of answering a question,
 * including the answer, verification status, and confidence score.
 */
public final class AnswerResult {
    private final Answer answer;
    private final VerificationResult verification;
    private final double confidence;
    private final String retrievalStrategy;

    @JsonCreator
    public AnswerResult(
            @JsonProperty("answer") Answer answer,
            @JsonProperty("verification") VerificationResult verification,
            @JsonProperty("confidence") double confidence,
            @JsonProperty("retrievalStrategy") String retrievalStrategy) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        if (verification == null) {
            throw new IllegalArgumentException("Verification cannot be null");
        }
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
        if (retrievalStrategy == null || retrievalStrategy.isBlank()) {
            throw new IllegalArgumentException("Retrieval strategy cannot be null or blank");
        }
        this.answer = answer;
        this.verification = verification;
        this.confidence = confidence;
        this.retrievalStrategy = retrievalStrategy;
    }

    @JsonGetter("answer")
    public Answer answer() {
        return answer;
    }

    @JsonGetter("verification")
    public VerificationResult verification() {
        return verification;
    }

    @JsonGetter("confidence")
    public double confidence() {
        return confidence;
    }

    @JsonGetter("retrievalStrategy")
    public String retrievalStrategy() {
        return retrievalStrategy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AnswerResult other)) return false;
        return Objects.equals(answer, other.answer) &&
               Objects.equals(verification, other.verification) &&
               Double.compare(confidence, other.confidence) == 0 &&
               Objects.equals(retrievalStrategy, other.retrievalStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer, verification, confidence, retrievalStrategy);
    }

    @Override
    public String toString() {
        return "AnswerResult[confidence=" + confidence +
               ", retrievalStrategy=" + retrievalStrategy +
               ", verification=" + verification.status() + "]";
    }
}
