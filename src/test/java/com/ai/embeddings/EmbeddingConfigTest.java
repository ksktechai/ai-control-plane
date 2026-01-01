package com.ai.embeddings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmbeddingConfigTest {

    @Test
    void shouldCreateConfig() {
        EmbeddingConfig config = new EmbeddingConfig("http://localhost:11434", "nomic-embed-text");

        assertThat(config.ollamaBaseUrl()).isEqualTo("http://localhost:11434");
        assertThat(config.model()).isEqualTo("nomic-embed-text");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        EmbeddingConfig c1 = new EmbeddingConfig("http://localhost:11434", "nomic-embed-text");
        EmbeddingConfig c2 = new EmbeddingConfig("http://localhost:11434", "nomic-embed-text");
        EmbeddingConfig c3 = new EmbeddingConfig("http://different:11434", "nomic-embed-text");
        EmbeddingConfig c4 = new EmbeddingConfig("http://localhost:11434", "different-model");

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).isNotEqualTo(c3);
        assertThat(c1).isNotEqualTo(c4);
        assertThat(c1).isEqualTo(c1);
        assertThat(c1).isNotEqualTo(null);
        assertThat(c1).isNotEqualTo("not an EmbeddingConfig");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        EmbeddingConfig c1 = new EmbeddingConfig("http://localhost:11434", "nomic-embed-text");
        EmbeddingConfig c2 = new EmbeddingConfig("http://localhost:11434", "nomic-embed-text");

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        EmbeddingConfig config = new EmbeddingConfig("http://localhost:11434", "nomic-embed-text");

        assertThat(config.toString())
            .contains("EmbeddingConfig")
            .contains("http://localhost:11434")
            .contains("nomic-embed-text");
    }

    @Test
    void shouldCreateDefaultConfig() {
        EmbeddingConfig config = new EmbeddingConfig();

        assertThat(config.ollamaBaseUrl()).isEqualTo("http://localhost:11434");
        assertThat(config.model()).isEqualTo("nomic-embed-text");
    }

    @Test
    void shouldRejectNullOllamaBaseUrl() {
        assertThatThrownBy(() -> new EmbeddingConfig(null, "nomic-embed-text"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ollama base URL cannot be null or blank");
    }

    @Test
    void shouldRejectBlankOllamaBaseUrl() {
        assertThatThrownBy(() -> new EmbeddingConfig("  ", "nomic-embed-text"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ollama base URL cannot be null or blank");
    }

    @Test
    void shouldRejectNullModel() {
        assertThatThrownBy(() -> new EmbeddingConfig("http://localhost:11434", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model cannot be null or blank");
    }

    @Test
    void shouldRejectBlankModel() {
        assertThatThrownBy(() -> new EmbeddingConfig("http://localhost:11434", "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model cannot be null or blank");
    }
}
