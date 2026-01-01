package com.ai.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LlmModelTest {

    @Test
    void shouldReturnCorrectOllamaName() {
        assertThat(LlmModel.LLAMA_3_2_3B.ollamaName()).isEqualTo("llama3.2:3b");
        assertThat(LlmModel.LLAMA_3_1_8B.ollamaName()).isEqualTo("llama3.1:8b");
        assertThat(LlmModel.QWEN_2_5_7B.ollamaName()).isEqualTo("qwen2.5:7b");
        assertThat(LlmModel.QWEN_2_5_14B.ollamaName()).isEqualTo("qwen2.5:14b");
        assertThat(LlmModel.MISTRAL_7B.ollamaName()).isEqualTo("mistral:7b");
        assertThat(LlmModel.PHI_3_MINI.ollamaName()).isEqualTo("phi3:mini");
        assertThat(LlmModel.DEEPSEEK_R1_1_5B.ollamaName()).isEqualTo("deepseek-r1:1.5b");
    }

    @Test
    void shouldReturnCorrectParameters() {
        assertThat(LlmModel.LLAMA_3_2_3B.parameters()).isEqualTo(3_000_000_000L);
        assertThat(LlmModel.LLAMA_3_1_8B.parameters()).isEqualTo(8_000_000_000L);
        assertThat(LlmModel.QWEN_2_5_7B.parameters()).isEqualTo(7_000_000_000L);
        assertThat(LlmModel.QWEN_2_5_14B.parameters()).isEqualTo(14_000_000_000L);
        assertThat(LlmModel.MISTRAL_7B.parameters()).isEqualTo(7_000_000_000L);
        assertThat(LlmModel.PHI_3_MINI.parameters()).isEqualTo(3_800_000_000L);
        assertThat(LlmModel.DEEPSEEK_R1_1_5B.parameters()).isEqualTo(1_500_000_000L);
    }

    @Test
    void shouldReturnCorrectContextWindow() {
        assertThat(LlmModel.LLAMA_3_2_3B.contextWindow()).isEqualTo(8192);
        assertThat(LlmModel.LLAMA_3_1_8B.contextWindow()).isEqualTo(8192);
        assertThat(LlmModel.QWEN_2_5_7B.contextWindow()).isEqualTo(32768);
        assertThat(LlmModel.QWEN_2_5_14B.contextWindow()).isEqualTo(32768);
        assertThat(LlmModel.MISTRAL_7B.contextWindow()).isEqualTo(8192);
        assertThat(LlmModel.PHI_3_MINI.contextWindow()).isEqualTo(4096);
        assertThat(LlmModel.DEEPSEEK_R1_1_5B.contextWindow()).isEqualTo(8192);
    }

    @Test
    void shouldIdentifySmallModels() {
        // Small models: <= 3.8B parameters
        assertThat(LlmModel.DEEPSEEK_R1_1_5B.isSmall()).isTrue(); // 1.5B
        assertThat(LlmModel.LLAMA_3_2_3B.isSmall()).isTrue(); // 3B
        assertThat(LlmModel.PHI_3_MINI.isSmall()).isTrue(); // 3.8B (boundary)

        // Not small
        assertThat(LlmModel.QWEN_2_5_7B.isSmall()).isFalse(); // 7B
        assertThat(LlmModel.LLAMA_3_1_8B.isSmall()).isFalse(); // 8B
        assertThat(LlmModel.QWEN_2_5_14B.isSmall()).isFalse(); // 14B
    }

    @Test
    void shouldIdentifyMediumModels() {
        // Medium models: > 3.8B and <= 8B parameters
        assertThat(LlmModel.QWEN_2_5_7B.isMedium()).isTrue(); // 7B
        assertThat(LlmModel.MISTRAL_7B.isMedium()).isTrue(); // 7B
        assertThat(LlmModel.LLAMA_3_1_8B.isMedium()).isTrue(); // 8B (boundary)

        // Not medium
        assertThat(LlmModel.DEEPSEEK_R1_1_5B.isMedium()).isFalse(); // 1.5B (too small)
        assertThat(LlmModel.LLAMA_3_2_3B.isMedium()).isFalse(); // 3B (too small)
        assertThat(LlmModel.PHI_3_MINI.isMedium()).isFalse(); // 3.8B (boundary - small)
        assertThat(LlmModel.QWEN_2_5_14B.isMedium()).isFalse(); // 14B (too large)
    }

    @Test
    void shouldIdentifyLargeModels() {
        // Large models: > 8B parameters
        assertThat(LlmModel.QWEN_2_5_14B.isLarge()).isTrue(); // 14B

        // Not large
        assertThat(LlmModel.DEEPSEEK_R1_1_5B.isLarge()).isFalse(); // 1.5B
        assertThat(LlmModel.LLAMA_3_2_3B.isLarge()).isFalse(); // 3B
        assertThat(LlmModel.PHI_3_MINI.isLarge()).isFalse(); // 3.8B
        assertThat(LlmModel.QWEN_2_5_7B.isLarge()).isFalse(); // 7B
        assertThat(LlmModel.MISTRAL_7B.isLarge()).isFalse(); // 7B
        assertThat(LlmModel.LLAMA_3_1_8B.isLarge()).isFalse(); // 8B (boundary - medium)
    }

    @Test
    void shouldHaveAllEnumValues() {
        assertThat(LlmModel.values()).hasSize(7);
    }

    @Test
    void shouldHaveMutuallyExclusiveSizeCategories() {
        for (LlmModel model : LlmModel.values()) {
            int categories = 0;
            if (model.isSmall()) categories++;
            if (model.isMedium()) categories++;
            if (model.isLarge()) categories++;

            assertThat(categories)
                    .as("Model %s should be in exactly one size category", model.name())
                    .isEqualTo(1);
        }
    }
}
