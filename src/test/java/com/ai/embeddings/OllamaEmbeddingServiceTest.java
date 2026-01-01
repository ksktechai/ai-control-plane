package com.ai.embeddings;

import com.ai.common.domain.Embedding;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class OllamaEmbeddingServiceTest {

    private MockWebServer mockServer;
    private EmbeddingService service;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        String baseUrl = mockServer.url("/").toString();
        EmbeddingConfig config = new EmbeddingConfig(baseUrl, "nomic-embed-text");
        service = new OllamaEmbeddingService(config);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void shouldGenerateEmbedding() {
        mockServer.enqueue(new MockResponse()
            .setBody("{\"embedding\":[0.1,0.2,0.3]}")
            .addHeader("Content-Type", "application/json"));

        Embedding embedding = service.generateEmbedding("sample text");

        assertThat(embedding).isNotNull();
        assertThat(embedding.vector()).hasSize(3);
        assertThat(embedding.model()).isEqualTo("nomic-embed-text");
    }

    @Test
    void shouldThrowExceptionForNullText() {
        assertThatThrownBy(() -> service.generateEmbedding(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Text cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForBlankText() {
        assertThatThrownBy(() -> service.generateEmbedding("  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Text cannot be null or blank");
    }

    @Test
    void shouldReturnCorrectDimension() {
        assertThat(service.getDimension()).isEqualTo(768);
    }

    @Test
    void shouldThrowExceptionOnNullEmbeddingInResponse() {
        mockServer.enqueue(new MockResponse()
            .setBody("{}")
            .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> service.generateEmbedding("sample text"))
            .isInstanceOf(EmbeddingException.class)
            .hasMessageContaining("Received null embedding from Ollama");
    }

    @Test
    void shouldThrowExceptionOnCompletelyNullResponse() {
        mockServer.enqueue(new MockResponse()
            .setResponseCode(204)  // No content
            .addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> service.generateEmbedding("sample text"))
            .isInstanceOf(EmbeddingException.class);
    }

    @Test
    void shouldThrowExceptionOnRestClientError() {
        mockServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> service.generateEmbedding("sample text"))
            .isInstanceOf(EmbeddingException.class)
            .hasMessageContaining("Failed to generate embedding");
    }

    @Test
    void shouldHandleMultipleDimensionEmbedding() {
        mockServer.enqueue(new MockResponse()
            .setBody("{\"embedding\":[0.1,0.2,0.3,0.4,0.5]}")
            .addHeader("Content-Type", "application/json"));

        Embedding embedding = service.generateEmbedding("sample text");

        assertThat(embedding).isNotNull();
        assertThat(embedding.vector()).hasSize(5);
        assertThat(embedding.vector()[0]).isEqualTo(0.1f);
        assertThat(embedding.vector()[4]).isEqualTo(0.5f);
    }
}
