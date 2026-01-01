package com.ai.dto;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ChatRequestTest {

    @Test
    void shouldCreateValidChatRequest() {
        ChatRequest request = new ChatRequest("What is AI?");

        assertThat(request.question()).isEqualTo("What is AI?");
    }

    @Test
    void shouldRejectNullQuestion() {
        assertThatThrownBy(() -> new ChatRequest(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question cannot be null or blank");
    }

    @Test
    void shouldRejectBlankQuestion() {
        assertThatThrownBy(() -> new ChatRequest("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question cannot be null or blank");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        ChatRequest r1 = new ChatRequest("What is AI?");
        ChatRequest r2 = new ChatRequest("What is AI?");
        ChatRequest r3 = new ChatRequest("Different question");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo("not a ChatRequest");
        assertThat(r1).isEqualTo(r1);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        ChatRequest r1 = new ChatRequest("What is AI?");
        ChatRequest r2 = new ChatRequest("What is AI?");

        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        ChatRequest request = new ChatRequest("What is AI?");

        assertThat(request.toString()).contains("ChatRequest").contains("What is AI?");
    }
}
