package com.ai.embeddings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for embedding service.
 */
@Configuration
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingConfig {
    private String ollamaBaseUrl = "http://localhost:11434";
    private String model = "nomic-embed-text";

    public EmbeddingConfig() {
    }

    public EmbeddingConfig(String ollamaBaseUrl, String model) {
        setOllamaBaseUrl(ollamaBaseUrl);
        setModel(model);
    }

    public String getOllamaBaseUrl() {
        return ollamaBaseUrl;
    }

    public void setOllamaBaseUrl(String ollamaBaseUrl) {
        if (ollamaBaseUrl == null || ollamaBaseUrl.isBlank()) {
            throw new IllegalArgumentException("Ollama base URL cannot be null or blank");
        }
        this.ollamaBaseUrl = ollamaBaseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model cannot be null or blank");
        }
        this.model = model;
    }

    public String ollamaBaseUrl() {
        return ollamaBaseUrl;
    }

    public String model() {
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmbeddingConfig that = (EmbeddingConfig) o;
        return ollamaBaseUrl.equals(that.ollamaBaseUrl) && model.equals(that.model);
    }

    @Override
    public int hashCode() {
        int result = ollamaBaseUrl.hashCode();
        result = 31 * result + model.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EmbeddingConfig[ollamaBaseUrl=" + ollamaBaseUrl + ", model=" + model + "]";
    }
}
