package com.ai.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AnswerTest {

    @Test
    void shouldCreateValidAnswer() {
        List<Citation> citations = List.of(
            new Citation("chunk-1", "doc-1", "text", 0.95)
        );
        Answer answer = new Answer("AI is artificial intelligence", citations, "llama3.1:8b");

        assertThat(answer.text()).isEqualTo("AI is artificial intelligence");
        assertThat(answer.citations()).hasSize(1);
        assertThat(answer.modelUsed()).isEqualTo("llama3.1:8b");
    }

    @Test
    void shouldCreateAnswerWithEmptyCitations() {
        Answer answer = new Answer("AI is artificial intelligence", List.of(), "llama3.1:8b");

        assertThat(answer.citations()).isEmpty();
    }

    @Test
    void shouldRejectNullText() {
        List<Citation> citations = List.of();
        assertThatThrownBy(() -> new Answer(null, citations, "llama3.1:8b"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Answer text cannot be null or blank");
    }

    @Test
    void shouldRejectBlankText() {
        List<Citation> citations = List.of();
        assertThatThrownBy(() -> new Answer("  ", citations, "llama3.1:8b"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Answer text cannot be null or blank");
    }

    @Test
    void shouldRejectNullCitations() {
        assertThatThrownBy(() -> new Answer("text", null, "llama3.1:8b"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Citations cannot be null");
    }

    @Test
    void shouldRejectNullModelUsed() {
        assertThatThrownBy(() -> new Answer("text", List.of(), null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model used cannot be null or blank");
    }

    @Test
    void shouldRejectBlankModelUsed() {
        assertThatThrownBy(() -> new Answer("text", List.of(), "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model used cannot be null or blank");
    }

    @Test
    void shouldReturnImmutableCitations() {
        List<Citation> citations = new ArrayList<>();
        citations.add(new Citation("chunk-1", "doc-1", "text", 0.95));
        Answer answer = new Answer("text", citations, "llama3.1:8b");

        assertThatThrownBy(() -> answer.citations().add(
            new Citation("chunk-2", "doc-2", "text2", 0.9)
        )).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        List<Citation> citations = List.of(
            new Citation("chunk-1", "doc-1", "text", 0.95)
        );
        List<Citation> differentCitations = List.of(
            new Citation("chunk-2", "doc-2", "different", 0.85)
        );
        Answer a1 = new Answer("AI is artificial intelligence", citations, "llama3.1:8b");
        Answer a2 = new Answer("AI is artificial intelligence", citations, "llama3.1:8b");
        Answer a3 = new Answer("Different answer", citations, "llama3.1:8b");
        Answer a4 = new Answer("AI is artificial intelligence", differentCitations, "llama3.1:8b");
        Answer a5 = new Answer("AI is artificial intelligence", citations, "different-model");

        assertThat(a1).isEqualTo(a2);
        assertThat(a1).isNotEqualTo(a3);
        assertThat(a1).isNotEqualTo(a4);
        assertThat(a1).isNotEqualTo(a5);
        assertThat(a1).isEqualTo(a1);
        assertThat(a1).isNotEqualTo(null);
        assertThat(a1).isNotEqualTo("not an Answer");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        List<Citation> citations = List.of(
            new Citation("chunk-1", "doc-1", "text", 0.95)
        );
        Answer a1 = new Answer("AI is artificial intelligence", citations, "llama3.1:8b");
        Answer a2 = new Answer("AI is artificial intelligence", citations, "llama3.1:8b");

        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        List<Citation> citations = List.of(
            new Citation("chunk-1", "doc-1", "text", 0.95)
        );
        Answer answer = new Answer("AI is artificial intelligence", citations, "llama3.1:8b");

        assertThat(answer.toString())
            .contains("Answer")
            .contains("AI is artificial intelligence")
            .contains("llama3.1:8b")
            .contains("citationCount=1");
    }
}
