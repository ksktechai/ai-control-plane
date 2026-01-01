package com.ai.embeddings.dto;

/** Response DTO for Ollama embedding API. */
public record OllamaEmbeddingResponse(Double[] embedding) {}
