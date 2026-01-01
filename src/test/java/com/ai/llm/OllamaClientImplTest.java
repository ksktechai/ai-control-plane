package com.ai.llm;

import com.ai.model.LlmModel;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.junit5.internal.MockWebServerExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockWebServerExtension.class)
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
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{\"model\":\"llama3.1:8b\",\"response\":\"AI is artificial intelligence\",\"done\":true}")
                                .addHeader("Content-Type", "application/json")
                                .build());

                String response = client.generate(LlmModel.LLAMA_3_1_8B, "What is AI?", 100);

                assertThat(response).isEqualTo("AI is artificial intelligence");
        }

        @Test
        void shouldCheckModelAvailability() {
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{\"models\":[{\"name\":\"llama3.1:8b\",\"model\":\"llama3.1:8b\",\"size\":4700000000}]}")
                                .addHeader("Content-Type", "application/json")
                                .build());

                boolean available = client.isModelAvailable(LlmModel.LLAMA_3_1_8B);

                assertThat(available).isTrue();
        }

        @Test
        void shouldReturnFalseWhenModelNotAvailable() {
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{\"models\":[]}")
                                .addHeader("Content-Type", "application/json")
                                .build());

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

        @Test
        void shouldThrowExceptionForZeroMaxTokens() {
                assertThatThrownBy(() -> client.generate(LlmModel.LLAMA_3_1_8B, "prompt", 0))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("Max tokens must be positive");
        }

        @Test
        void shouldThrowExceptionForBlankPrompt() {
                assertThatThrownBy(() -> client.generate(LlmModel.LLAMA_3_1_8B, "   ", 100))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("Prompt cannot be null or blank");
        }

        @Test
        void shouldThrowExceptionForEmptyPrompt() {
                assertThatThrownBy(() -> client.generate(LlmModel.LLAMA_3_1_8B, "", 100))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("Prompt cannot be null or blank");
        }

        @Test
        void shouldThrowExceptionForNullModelInIsModelAvailable() {
                assertThatThrownBy(() -> client.isModelAvailable(null))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("Model cannot be null");
        }

        @Test
        void shouldThrowOllamaExceptionForNullResponse() {
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{}")
                                .addHeader("Content-Type", "application/json")
                                .build());

                Throwable thrown = catchThrowable(() -> client.generate(LlmModel.LLAMA_3_1_8B, "What is AI?", 100));

                assertThat(thrown).isInstanceOf(OllamaException.class);
                // Accept either message depending on whether RestClient throws first or
                // deserializes to nulls
                assertThat(thrown.getMessage()).satisfiesAnyOf(
                                msg -> assertThat(msg).contains("Received null response from Ollama"),
                                msg -> assertThat(msg).contains("Failed to generate response from Ollama"));
        }

        @Test
        void shouldThrowOllamaExceptionForNullResponseText() {
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{\"model\":\"llama3.1:8b\",\"response\":null,\"done\":true}")
                                .addHeader("Content-Type", "application/json")
                                .build());

                assertThatThrownBy(() -> client.generate(LlmModel.LLAMA_3_1_8B, "What is AI?", 100))
                                .isInstanceOf(OllamaException.class)
                                .hasMessageContaining("Received null response from Ollama");
        }

        @Test
        void shouldThrowOllamaExceptionForServerError() {
                mockServer.enqueue(new MockResponse.Builder()
                                .code(500)
                                .body("Internal Server Error")
                                .build());

                assertThatThrownBy(() -> client.generate(LlmModel.LLAMA_3_1_8B, "What is AI?", 100))
                                .isInstanceOf(OllamaException.class)
                                .hasMessageContaining("Failed to generate response from Ollama");
        }

        @Test
        void shouldReturnFalseForNullModelListResponse() {
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{}")
                                .addHeader("Content-Type", "application/json")
                                .build());

                boolean available = client.isModelAvailable(LlmModel.LLAMA_3_1_8B);

                assertThat(available).isFalse();
        }

        @Test
        void shouldReturnFalseForNullModelsArray() {
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{\"models\":null}")
                                .addHeader("Content-Type", "application/json")
                                .build());

                boolean available = client.isModelAvailable(LlmModel.LLAMA_3_1_8B);

                assertThat(available).isFalse();
        }

        @Test
        void shouldReturnFalseOnConnectionError() {
                mockServer.enqueue(new MockResponse.Builder()
                                .code(500)
                                .body("Server Error")
                                .build());

                boolean available = client.isModelAvailable(LlmModel.LLAMA_3_1_8B);

                assertThat(available).isFalse();
        }

        @Test
        void shouldCheckMultipleModelsInList() {
                mockServer.enqueue(new MockResponse.Builder()
                                .body("{\"models\":[{\"name\":\"qwen2.5:7b\",\"model\":\"qwen2.5:7b\",\"size\":100},{\"name\":\"llama3.1:8b\",\"model\":\"llama3.1:8b\",\"size\":100},{\"name\":\"mistral:7b\",\"model\":\"mistral:7b\",\"size\":100}]}")
                                .addHeader("Content-Type", "application/json")
                                .build());

                boolean available = client.isModelAvailable(LlmModel.MISTRAL_7B);

                assertThat(available).isTrue();
        }
}
