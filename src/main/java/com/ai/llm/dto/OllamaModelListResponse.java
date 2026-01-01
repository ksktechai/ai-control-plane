package com.ai.llm.dto;

import java.util.List;

/**
 * Response DTO for Ollama list models API.
 */
public record OllamaModelListResponse(
    List<OllamaModelInfo> models
) {
    public record OllamaModelInfo(
        String name,
        String model,
        long size
    ) {
    }
}
