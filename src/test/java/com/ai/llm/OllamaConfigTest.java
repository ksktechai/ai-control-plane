package com.ai.llm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OllamaConfigTest {

    @Test
    void shouldCreateWithDefaultBaseUrl() {
        OllamaConfig config = new OllamaConfig();

        assertThat(config.baseUrl()).isEqualTo("http://localhost:11434");
        assertThat(config.getBaseUrl()).isEqualTo("http://localhost:11434");
    }

    @Test
    void shouldCreateWithCustomBaseUrl() {
        OllamaConfig config = new OllamaConfig("http://custom:8080");

        assertThat(config.baseUrl()).isEqualTo("http://custom:8080");
    }

    @Test
    void shouldSetBaseUrl() {
        OllamaConfig config = new OllamaConfig();

        config.setBaseUrl("http://new-url:9090");

        assertThat(config.baseUrl()).isEqualTo("http://new-url:9090");
    }

    @Test
    void shouldThrowExceptionForNullBaseUrl() {
        OllamaConfig config = new OllamaConfig();

        assertThatThrownBy(() -> config.setBaseUrl(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ollama base URL cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForBlankBaseUrl() {
        OllamaConfig config = new OllamaConfig();

        assertThatThrownBy(() -> config.setBaseUrl("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ollama base URL cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForEmptyBaseUrl() {
        OllamaConfig config = new OllamaConfig();

        assertThatThrownBy(() -> config.setBaseUrl(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ollama base URL cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForNullBaseUrlInConstructor() {
        assertThatThrownBy(() -> new OllamaConfig(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ollama base URL cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForBlankBaseUrlInConstructor() {
        assertThatThrownBy(() -> new OllamaConfig("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ollama base URL cannot be null or blank");
    }
}
