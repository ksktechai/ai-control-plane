package com.ai.embeddings;

import com.ai.domain.Embedding;
import com.ai.embeddings.dto.OllamaEmbeddingRequest;
import com.ai.embeddings.dto.OllamaEmbeddingResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Embedding service using Ollama's embedding models. */
@Service
public class OllamaEmbeddingService implements EmbeddingService {
    private static final Logger logger = LogManager.getLogger(OllamaEmbeddingService.class);
    private static final String DEFAULT_MODEL = "nomic-embed-text";
    private static final int EMBEDDING_DIMENSION = 768;

    private final RestClient restClient;
    private final String model;

    public OllamaEmbeddingService(EmbeddingConfig config) {
        this.model = config.model();
        this.restClient = RestClient.builder().baseUrl(config.ollamaBaseUrl()).build();
        logger.info("Initialized EmbeddingService with model: {}", model);
    }

    @Override
    public Embedding generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or blank");
        }

        OllamaEmbeddingRequest request = new OllamaEmbeddingRequest(model, text);

        logger.debug("Generating embedding for text of length: {}", text.length());

        try {
            OllamaEmbeddingResponse response =
                    restClient
                            .post()
                            .uri("/api/embeddings")
                            .body(request)
                            .retrieve()
                            .body(OllamaEmbeddingResponse.class);

            if (response == null || response.embedding() == null) {
                throw new EmbeddingException("Received null embedding from Ollama");
            }

            float[] vector = new float[response.embedding().length];
            for (int i = 0; i < response.embedding().length; i++) {
                vector[i] = response.embedding()[i].floatValue();
            }

            logger.debug("Generated embedding with dimension: {}", vector.length);
            return new Embedding(vector, model);

        } catch (RestClientException e) {
            logger.error("Failed to generate embedding: {}", e.getMessage());
            throw new EmbeddingException("Failed to generate embedding", e);
        }
    }

    @Override
    public int getDimension() {
        return EMBEDDING_DIMENSION;
    }
}
