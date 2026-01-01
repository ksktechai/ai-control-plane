package com.ai.embeddings.dto;

/** Request DTO for Ollama embedding API. */
public record OllamaEmbeddingRequest(String model, String prompt) {}
