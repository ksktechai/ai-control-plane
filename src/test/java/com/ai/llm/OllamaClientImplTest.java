package com.ai.llm;

import com.ai.common.model.LlmModel;
import com.ai.llm.dto.OllamaGenerateResponse;
import com.ai.llm.dto.OllamaModelListResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OllamaClientImplTest {

    private MockWebServer mockServer;
    private OllamaClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        String baseUrl = mockServer.url("/").toString();
        OllamaConfig config = new OllamaConfig(baseUrl);
        client = new OllamaClientImpl(config);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void shouldGenerateResponse() {
        mockServer.enqueue(new MockResponse()
            .setBody("{\"model\":\"llama3.1:8b\",\"response\":\"AI is artificial intelligence\",\"done\":true}")
            .addHeader("Content-Type", "application/json"));

        String response = client.generate(LlmModel.LLAMA_3_1_8B, "What is AI?", 100);

        assertThat(response).isEqualTo("AI is artificial intelligence");
    }

    @Test
    void shouldCheckModelAvailability() {
        mockServer.enqueue(new MockResponse()
            .setBody("{\"models\":[{\"name\":\"llama3.1:8b\",\"model\":\"llama3.1:8b\",\"size\":4700000000}]}")
            .addHeader("Content-Type", "application/json"));

        boolean available = client.isModelAvailable(LlmModel.LLAMA_3_1_8B);

        assertThat(available).isTrue();
    }

    @Test
    void shouldReturnFalseWhenModelNotAvailable() {
        mockServer.enqueue(new MockResponse()
            .setBody("{\"models\":[]}")
            .addHeader("Content-Type", "application/json"));

        boolean available = client.isModelAvailable(LlmModel.LLAMA_3_1_8B);

        assertThat(available).isFalse();
    }

    @Test
    void shouldThrowExceptionForNullModel() {
        assertThatThrownBy(() -> client.generate(null, "prompt", 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model cannot be null");
    }

    @Test
    void shouldThrowExceptionForNullPrompt() {
        assertThatThrownBy(() -> client.generate(LlmModel.LLAMA_3_1_8B, null, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Prompt cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForInvalidMaxTokens() {
        assertThatThrownBy(() -> client.generate(LlmModel.LLAMA_3_1_8B, "prompt", -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Max tokens must be positive");
    }
}
