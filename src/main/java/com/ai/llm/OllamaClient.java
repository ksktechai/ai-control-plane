package com.ai.llm;

import com.ai.common.model.LlmModel;

/**
 * Client for interacting with Ollama LLM runtime.
 */
public interface OllamaClient {

    /**
     * Generates a response from the specified model given a prompt.
     *
     * @param model The LLM model to use
     * @param prompt The input prompt
     * @param maxTokens Maximum tokens to generate
     * @return The generated response text
     */
    String generate(LlmModel model, String prompt, int maxTokens);

    /**
     * Checks if the specified model is available in Ollama.
     *
     * @param model The model to check
     * @return true if the model is available, false otherwise
     */
    boolean isModelAvailable(LlmModel model);
}
