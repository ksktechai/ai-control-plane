package com.ai.embeddings.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OllamaEmbeddingRequestTest {

    @Test
    void shouldCreateRequest() {
        OllamaEmbeddingRequest request = new OllamaEmbeddingRequest("nomic-embed-text", "sample text");

        assertThat(request.model()).isEqualTo("nomic-embed-text");
        assertThat(request.prompt()).isEqualTo("sample text");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        OllamaEmbeddingRequest r1 = new OllamaEmbeddingRequest("nomic-embed-text", "sample text");
        OllamaEmbeddingRequest r2 = new OllamaEmbeddingRequest("nomic-embed-text", "sample text");
        OllamaEmbeddingRequest r3 = new OllamaEmbeddingRequest("different-model", "sample text");
        OllamaEmbeddingRequest r4 = new OllamaEmbeddingRequest("nomic-embed-text", "different text");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
        assertThat(r1).isNotEqualTo(r4);
        assertThat(r1).isEqualTo(r1);
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo("not a request");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        OllamaEmbeddingRequest r1 = new OllamaEmbeddingRequest("nomic-embed-text", "sample text");
        OllamaEmbeddingRequest r2 = new OllamaEmbeddingRequest("nomic-embed-text", "sample text");

        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        OllamaEmbeddingRequest request = new OllamaEmbeddingRequest("nomic-embed-text", "sample text");

        assertThat(request.toString())
            .contains("OllamaEmbeddingRequest")
            .contains("nomic-embed-text")
            .contains("sample text");
    }
}
