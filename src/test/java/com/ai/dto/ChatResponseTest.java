package com.ai.dto;

import static org.assertj.core.api.Assertions.*;

import com.ai.domain.Citation;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChatResponseTest {

    @Test
    void shouldCreateValidChatResponse() {
        List<Citation> citations = List.of(new Citation("chunk-1", "doc-1", "text", 0.95));

        ChatResponse response =
                new ChatResponse(
                        "AI is artificial intelligence",
                        citations,
                        0.9,
                        "llama3.1:8b",
                        "SIMPLE",
                        "GROUNDED");

        assertThat(response.answer()).isEqualTo("AI is artificial intelligence");
        assertThat(response.citations()).hasSize(1);
        assertThat(response.confidence()).isEqualTo(0.9);
        assertThat(response.modelUsed()).isEqualTo("llama3.1:8b");
        assertThat(response.retrievalStrategy()).isEqualTo("SIMPLE");
        assertThat(response.verificationStatus()).isEqualTo("GROUNDED");
    }

    @Test
    void shouldRejectNullAnswer() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        null, List.of(), 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Answer cannot be null or blank");
    }

    @Test
    void shouldRejectBlankAnswer() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "  ", List.of(), 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Answer cannot be null or blank");
    }

    @Test
    void shouldRejectNullCitations() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer", null, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Citations cannot be null");
    }

    @Test
    void shouldRejectNegativeConfidence() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer",
                                        List.of(),
                                        -0.1,
                                        "llama3.1:8b",
                                        "SIMPLE",
                                        "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Confidence must be between 0.0 and 1.0");
    }

    @Test
    void shouldRejectConfidenceAboveOne() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer",
                                        List.of(),
                                        1.1,
                                        "llama3.1:8b",
                                        "SIMPLE",
                                        "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Confidence must be between 0.0 and 1.0");
    }

    @Test
    void shouldRejectNullModelUsed() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer", List.of(), 0.9, null, "SIMPLE", "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Model used cannot be null or blank");
    }

    @Test
    void shouldRejectBlankModelUsed() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer", List.of(), 0.9, "  ", "SIMPLE", "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Model used cannot be null or blank");
    }

    @Test
    void shouldRejectNullRetrievalStrategy() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer", List.of(), 0.9, "llama3.1:8b", null, "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Retrieval strategy cannot be null or blank");
    }

    @Test
    void shouldRejectBlankRetrievalStrategy() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer", List.of(), 0.9, "llama3.1:8b", "  ", "GROUNDED"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Retrieval strategy cannot be null or blank");
    }

    @Test
    void shouldRejectNullVerificationStatus() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer", List.of(), 0.9, "llama3.1:8b", "SIMPLE", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Verification status cannot be null or blank");
    }

    @Test
    void shouldRejectBlankVerificationStatus() {
        assertThatThrownBy(
                        () ->
                                new ChatResponse(
                                        "answer", List.of(), 0.9, "llama3.1:8b", "SIMPLE", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Verification status cannot be null or blank");
    }

    @Test
    void shouldReturnImmutableCitations() {
        List<Citation> citations = new ArrayList<>();
        citations.add(new Citation("chunk-1", "doc-1", "text", 0.95));

        ChatResponse response =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");

        assertThatThrownBy(
                        () ->
                                response.citations()
                                        .add(new Citation("chunk-2", "doc-2", "text2", 0.9)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        List<Citation> citations = List.of(new Citation("chunk-1", "doc-1", "text", 0.95));
        List<Citation> differentCitations =
                List.of(new Citation("chunk-2", "doc-2", "different", 0.85));

        ChatResponse r1 =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");
        ChatResponse r2 =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");
        ChatResponse r3 =
                new ChatResponse("different", citations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");
        ChatResponse r4 =
                new ChatResponse(
                        "answer", differentCitations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");
        ChatResponse r5 =
                new ChatResponse("answer", citations, 0.8, "llama3.1:8b", "SIMPLE", "GROUNDED");
        ChatResponse r6 =
                new ChatResponse("answer", citations, 0.9, "different-model", "SIMPLE", "GROUNDED");
        ChatResponse r7 =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "DEEP", "GROUNDED");
        ChatResponse r8 =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "SIMPLE", "UNGROUNDED");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
        assertThat(r1).isNotEqualTo(r4);
        assertThat(r1).isNotEqualTo(r5);
        assertThat(r1).isNotEqualTo(r6);
        assertThat(r1).isNotEqualTo(r7);
        assertThat(r1).isNotEqualTo(r8);
        assertThat(r1).isEqualTo(r1);
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo("not a ChatResponse");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        List<Citation> citations = List.of(new Citation("chunk-1", "doc-1", "text", 0.95));

        ChatResponse r1 =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");
        ChatResponse r2 =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");

        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        List<Citation> citations = List.of(new Citation("chunk-1", "doc-1", "text", 0.95));

        ChatResponse response =
                new ChatResponse("answer", citations, 0.9, "llama3.1:8b", "SIMPLE", "GROUNDED");

        assertThat(response.toString())
                .contains("ChatResponse")
                .contains("0.9")
                .contains("llama3.1:8b")
                .contains("citationCount=1");
    }
}
