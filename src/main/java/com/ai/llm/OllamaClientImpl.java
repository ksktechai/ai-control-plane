package com.ai.llm;

import com.ai.common.model.LlmModel;
import com.ai.llm.dto.OllamaGenerateRequest;
import com.ai.llm.dto.OllamaGenerateResponse;
import com.ai.llm.dto.OllamaModelListResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of OllamaClient using Spring RestClient.
 */
@Component
public class OllamaClientImpl implements OllamaClient {
    private static final Logger logger = LogManager.getLogger(OllamaClientImpl.class);

    private final RestClient restClient;
    private final String ollamaBaseUrl;

    public OllamaClientImpl(OllamaConfig config) {
        this.ollamaBaseUrl = config.baseUrl();
        this.restClient = RestClient.builder()
            .baseUrl(ollamaBaseUrl)
            .build();
        logger.info("Initialized OllamaClient with base URL: {}", ollamaBaseUrl);
    }

    @Override
    public String generate(LlmModel model, String prompt, int maxTokens) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or blank");
        }
        if (maxTokens <= 0) {
            throw new IllegalArgumentException("Max tokens must be positive");
        }

        OllamaGenerateRequest request = new OllamaGenerateRequest(
            model.ollamaName(),
            prompt,
            maxTokens,
            false
        );

        logger.debug("Generating response with model: {}, maxTokens: {}", model.ollamaName(), maxTokens);

        try {
            OllamaGenerateResponse response = restClient.post()
                .uri("/api/generate")
                .body(request)
                .retrieve()
                .body(OllamaGenerateResponse.class);

            if (response == null || response.response() == null) {
                throw new OllamaException("Received null response from Ollama");
            }

            logger.debug("Generated response of length: {}", response.response().length());
            return response.response();

        } catch (RestClientException e) {
            logger.error("Failed to generate response from Ollama: {}", e.getMessage());
            throw new OllamaException("Failed to generate response from Ollama", e);
        }
    }

    @Override
    public boolean isModelAvailable(LlmModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }

        try {
            OllamaModelListResponse response = restClient.get()
                .uri("/api/tags")
                .retrieve()
                .body(OllamaModelListResponse.class);

            if (response == null || response.models() == null) {
                logger.warn("Received null or empty model list from Ollama");
                return false;
            }

            Set<String> availableModels = response.models().stream()
                .map(OllamaModelListResponse.OllamaModelInfo::name)
                .collect(Collectors.toSet());

            boolean available = availableModels.contains(model.ollamaName());
            logger.debug("Model {} availability: {}", model.ollamaName(), available);
            return available;

        } catch (RestClientException e) {
            logger.error("Failed to check model availability: {}", e.getMessage());
            return false;
        }
    }
}
