package com.ai.common.model;

/**
 * Available open-source LLM models.
 */
public enum LlmModel {
    LLAMA_3_2_3B("llama3.2:3b", 3_000_000_000L, 8192),
    LLAMA_3_1_8B("llama3.1:8b", 8_000_000_000L, 8192),
    QWEN_2_5_7B("qwen2.5:7b", 7_000_000_000L, 32768),
    QWEN_2_5_14B("qwen2.5:14b", 14_000_000_000L, 32768),
    MISTRAL_7B("mistral:7b", 7_000_000_000L, 8192),
    PHI_3_MINI("phi3:mini", 3_800_000_000L, 4096),
    DEEPSEEK_R1_1_5B("deepseek-r1:1.5b", 1_500_000_000L, 8192);

    private final String ollamaName;
    private final long parameters;
    private final int contextWindow;

    LlmModel(String ollamaName, long parameters, int contextWindow) {
        this.ollamaName = ollamaName;
        this.parameters = parameters;
        this.contextWindow = contextWindow;
    }

    public String ollamaName() {
        return ollamaName;
    }

    public long parameters() {
        return parameters;
    }

    public int contextWindow() {
        return contextWindow;
    }

    public boolean isSmall() {
        return parameters <= 3_800_000_000L;
    }

    public boolean isMedium() {
        return parameters > 3_800_000_000L && parameters <= 8_000_000_000L;
    }

    public boolean isLarge() {
        return parameters > 8_000_000_000L;
    }
}
