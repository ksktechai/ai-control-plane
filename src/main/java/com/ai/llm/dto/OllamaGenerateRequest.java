package com.ai.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for Ollama generate API.
 */
public record OllamaGenerateRequest(
    String model,
    String prompt,
    @JsonProperty("num_predict") int numPredict,
    boolean stream
) {
    public OllamaGenerateRequest {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model cannot be null or blank");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or blank");
        }
        if (numPredict <= 0) {
            throw new IllegalArgumentException("numPredict must be positive");
        }
    }
}
