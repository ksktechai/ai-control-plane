package com.ai.llm.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OllamaGenerateResponseTest {

    @Test
    void shouldCreateValidResponse() {
        OllamaGenerateResponse response = new OllamaGenerateResponse("llama3.1:8b", "AI is artificial intelligence",
                true);

        assertThat(response.model()).isEqualTo("llama3.1:8b");
        assertThat(response.response()).isEqualTo("AI is artificial intelligence");
        assertThat(response.done()).isTrue();
    }

    @Test
    void shouldHandleNullResponse() {
        OllamaGenerateResponse response = new OllamaGenerateResponse("model", null, false);

        assertThat(response.response()).isNull();
        assertThat(response.done()).isFalse();
    }

    @Test
    void shouldHandleEmptyResponse() {
        OllamaGenerateResponse response = new OllamaGenerateResponse("model", "", true);

        assertThat(response.response()).isEmpty();
    }
}

class OllamaModelListResponseTest {

    @Test
    void shouldCreateResponseWithModels() {
        OllamaModelListResponse.OllamaModelInfo model1 = new OllamaModelListResponse.OllamaModelInfo("llama3.1:8b",
                "llama3.1:8b", 4700000000L);
        OllamaModelListResponse.OllamaModelInfo model2 = new OllamaModelListResponse.OllamaModelInfo("qwen2.5:7b",
                "qwen2.5:7b", 4100000000L);

        OllamaModelListResponse response = new OllamaModelListResponse(List.of(model1, model2));

        assertThat(response.models()).hasSize(2);
        assertThat(response.models().get(0).name()).isEqualTo("llama3.1:8b");
        assertThat(response.models().get(1).name()).isEqualTo("qwen2.5:7b");
    }

    @Test
    void shouldHandleEmptyModelList() {
        OllamaModelListResponse response = new OllamaModelListResponse(List.of());

        assertThat(response.models()).isEmpty();
    }

    @Test
    void shouldHandleNullModelList() {
        OllamaModelListResponse response = new OllamaModelListResponse(null);

        assertThat(response.models()).isNull();
    }

    @Test
    void shouldAccessModelInfoProperties() {
        OllamaModelListResponse.OllamaModelInfo modelInfo = new OllamaModelListResponse.OllamaModelInfo("mistral:7b",
                "mistral:7b", 7000000000L);

        assertThat(modelInfo.name()).isEqualTo("mistral:7b");
        assertThat(modelInfo.model()).isEqualTo("mistral:7b");
        assertThat(modelInfo.size()).isEqualTo(7000000000L);
    }
}
