package com.ai.llm.dto;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OllamaGenerateRequestTest {

    @Test
    void shouldCreateValidRequest() {
        OllamaGenerateRequest request =
                new OllamaGenerateRequest("llama3.1:8b", "Hello", 100, false);

        assertThat(request.model()).isEqualTo("llama3.1:8b");
        assertThat(request.prompt()).isEqualTo("Hello");
        assertThat(request.numPredict()).isEqualTo(100);
        assertThat(request.stream()).isFalse();
    }

    @Test
    void shouldCreateRequestWithStream() {
        OllamaGenerateRequest request =
                new OllamaGenerateRequest("qwen2.5:7b", "Test prompt", 200, true);

        assertThat(request.stream()).isTrue();
    }

    @Test
    void shouldThrowExceptionForNullModel() {
        assertThatThrownBy(() -> new OllamaGenerateRequest(null, "prompt", 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Model cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForBlankModel() {
        assertThatThrownBy(() -> new OllamaGenerateRequest("   ", "prompt", 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Model cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForEmptyModel() {
        assertThatThrownBy(() -> new OllamaGenerateRequest("", "prompt", 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Model cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForNullPrompt() {
        assertThatThrownBy(() -> new OllamaGenerateRequest("model", null, 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prompt cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForBlankPrompt() {
        assertThatThrownBy(() -> new OllamaGenerateRequest("model", "   ", 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prompt cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForEmptyPrompt() {
        assertThatThrownBy(() -> new OllamaGenerateRequest("model", "", 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prompt cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForZeroNumPredict() {
        assertThatThrownBy(() -> new OllamaGenerateRequest("model", "prompt", 0, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("numPredict must be positive");
    }

    @Test
    void shouldThrowExceptionForNegativeNumPredict() {
        assertThatThrownBy(() -> new OllamaGenerateRequest("model", "prompt", -1, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("numPredict must be positive");
    }
}
