package com.ai.llm.dto;

/** Response DTO for Ollama generate API. */
public record OllamaGenerateResponse(String model, String response, boolean done) {}
