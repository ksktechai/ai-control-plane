package com.ai.embeddings.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OllamaEmbeddingResponseTest {

    @Test
    void shouldCreateResponse() {
        Double[] embedding = {0.1, 0.2, 0.3};
        OllamaEmbeddingResponse response = new OllamaEmbeddingResponse(embedding);

        assertThat(response.embedding()).hasSize(3);
        assertThat(response.embedding()[0]).isEqualTo(0.1);
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Double[] embedding1 = {0.1, 0.2, 0.3};
        Double[] embedding3 = {0.4, 0.5, 0.6};

        OllamaEmbeddingResponse r1 = new OllamaEmbeddingResponse(embedding1);
        OllamaEmbeddingResponse r2 = new OllamaEmbeddingResponse(embedding1); // Same reference
        OllamaEmbeddingResponse r3 = new OllamaEmbeddingResponse(embedding3);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
        assertThat(r1).isEqualTo(r1);
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo("not a response");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Double[] embedding1 = {0.1, 0.2, 0.3};

        OllamaEmbeddingResponse r1 = new OllamaEmbeddingResponse(embedding1);
        OllamaEmbeddingResponse r2 = new OllamaEmbeddingResponse(embedding1); // Same reference

        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Double[] embedding = {0.1, 0.2, 0.3};
        OllamaEmbeddingResponse response = new OllamaEmbeddingResponse(embedding);

        assertThat(response.toString())
            .contains("OllamaEmbeddingResponse")
            .contains("embedding");
    }
}
