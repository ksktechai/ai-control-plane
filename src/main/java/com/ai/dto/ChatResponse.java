package com.ai.common.dto;

import com.ai.common.domain.Citation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * Response DTO for chat endpoint.
 */
public final class ChatResponse {
    private final String answer;
    private final List<Citation> citations;
    private final double confidence;
    private final String modelUsed;
    private final String retrievalStrategy;
    private final String verificationStatus;

    @JsonCreator
    public ChatResponse(
            @JsonProperty("answer") String answer,
            @JsonProperty("citations") List<Citation> citations,
            @JsonProperty("confidence") double confidence,
            @JsonProperty("modelUsed") String modelUsed,
            @JsonProperty("retrievalStrategy") String retrievalStrategy,
            @JsonProperty("verificationStatus") String verificationStatus) {
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Answer cannot be null or blank");
        }
        if (citations == null) {
            throw new IllegalArgumentException("Citations cannot be null");
        }
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
        if (modelUsed == null || modelUsed.isBlank()) {
            throw new IllegalArgumentException("Model used cannot be null or blank");
        }
        if (retrievalStrategy == null || retrievalStrategy.isBlank()) {
            throw new IllegalArgumentException("Retrieval strategy cannot be null or blank");
        }
        if (verificationStatus == null || verificationStatus.isBlank()) {
            throw new IllegalArgumentException("Verification status cannot be null or blank");
        }
        this.answer = answer;
        this.citations = List.copyOf(citations);
        this.confidence = confidence;
        this.modelUsed = modelUsed;
        this.retrievalStrategy = retrievalStrategy;
        this.verificationStatus = verificationStatus;
    }

    @JsonGetter("answer")
    public String answer() {
        return answer;
    }

    @JsonGetter("citations")
    public List<Citation> citations() {
        return citations;
    }

    @JsonGetter("confidence")
    public double confidence() {
        return confidence;
    }

    @JsonGetter("modelUsed")
    public String modelUsed() {
        return modelUsed;
    }

    @JsonGetter("retrievalStrategy")
    public String retrievalStrategy() {
        return retrievalStrategy;
    }

    @JsonGetter("verificationStatus")
    public String verificationStatus() {
        return verificationStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChatResponse other)) return false;
        return Objects.equals(answer, other.answer) &&
               Objects.equals(citations, other.citations) &&
               Double.compare(confidence, other.confidence) == 0 &&
               Objects.equals(modelUsed, other.modelUsed) &&
               Objects.equals(retrievalStrategy, other.retrievalStrategy) &&
               Objects.equals(verificationStatus, other.verificationStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer, citations, confidence, modelUsed,
                          retrievalStrategy, verificationStatus);
    }

    @Override
    public String toString() {
        return "ChatResponse[confidence=" + confidence +
               ", modelUsed=" + modelUsed +
               ", citationCount=" + citations.size() + "]";
    }
}
