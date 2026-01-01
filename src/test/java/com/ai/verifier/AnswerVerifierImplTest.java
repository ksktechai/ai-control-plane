package com.ai.verifier;

import com.ai.domain.*;
import com.ai.model.LlmModel;
import com.ai.llm.OllamaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerVerifierImplTest {

    @Mock
    private OllamaClient ollamaClient;

    private AnswerVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new AnswerVerifierImpl(ollamaClient);
    }

    @Test
    void shouldVerifyGroundedAnswer() {
        Embedding embedding = new Embedding(new float[] { 0.1f }, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult context = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("AI stands for artificial intelligence", List.of(), "llama3.1:8b");

        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenReturn("1. AI stands for artificial intelligence");
        when(ollamaClient.generate(any(LlmModel.class), contains("Does the following"), anyInt()))
                .thenReturn("yes");

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(VerificationStatus.GROUNDED);
        assertThat(result.groundingScore()).isGreaterThanOrEqualTo(0.9);
    }

    @Test
    void shouldVerifyUngroundedAnswer() {
        Embedding embedding = new Embedding(new float[] { 0.1f }, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult context = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("AI is magic", List.of(), "llama3.1:8b");

        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenReturn("1. AI is magic");
        when(ollamaClient.generate(any(LlmModel.class), contains("Does the following"), anyInt()))
                .thenReturn("no");

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.status()).isIn(VerificationStatus.UNGROUNDED, VerificationStatus.PARTIALLY_GROUNDED);
        assertThat(result.groundingScore()).isLessThan(0.9);
    }

    @Test
    void shouldThrowExceptionForNullAnswer() {
        RetrievalResult context = new RetrievalResult(List.of(), "SIMPLE", 100L);

        assertThatThrownBy(() -> verifier.verify(null, context))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Answer cannot be null");
    }

    @Test
    void shouldThrowExceptionForNullContext() {
        Answer answer = new Answer("test", List.of(), "llama3.1:8b");

        assertThatThrownBy(() -> verifier.verify(answer, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Context cannot be null");
    }

    @Test
    void shouldReturnUngroundedWhenNoChunks() {
        RetrievalResult context = new RetrievalResult(List.of(), "SIMPLE", 100L);
        Answer answer = new Answer("AI is artificial intelligence", List.of(), "llama3.1:8b");

        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenReturn("1. AI is artificial intelligence");

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(VerificationStatus.UNGROUNDED);
    }

    @Test
    void shouldHandleLlmExtractionFailure() {
        Embedding embedding = new Embedding(new float[] { 0.1f }, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult context = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("AI stands for artificial intelligence", List.of(), "llama3.1:8b");

        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenThrow(new RuntimeException("LLM error"));
        when(ollamaClient.generate(any(LlmModel.class), contains("Does the following"), anyInt()))
                .thenReturn("yes");

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.claims()).isNotEmpty();
    }

    @Test
    void shouldHandleLlmVerificationFailure() {
        Embedding embedding = new Embedding(new float[] { 0.1f }, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult context = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("AI stands for artificial intelligence", List.of(), "llama3.1:8b");

        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenReturn("1. AI stands for artificial intelligence");
        when(ollamaClient.generate(any(LlmModel.class), contains("Does the following"), anyInt()))
                .thenThrow(new RuntimeException("Verification error"));

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.groundingScore()).isEqualTo(0.0);
        assertThat(result.status()).isEqualTo(VerificationStatus.UNGROUNDED);
    }

    @Test
    void shouldVerifyPartiallyGroundedAnswer() {
        Embedding embedding = new Embedding(new float[] { 0.1f }, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult context = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("Multiple claims here", List.of(), "llama3.1:8b");

        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenReturn("1. First claim is true\n2. Second claim is false");
        when(ollamaClient.generate(any(LlmModel.class), contains("Does the following"), anyInt()))
                .thenReturn("yes")
                .thenReturn("no");

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.groundingScore()).isEqualTo(0.5);
        assertThat(result.status()).isEqualTo(VerificationStatus.PARTIALLY_GROUNDED);
    }

    @Test
    void shouldParseClaimsWithDifferentFormats() {
        Embedding embedding = new Embedding(new float[] { 0.1f }, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult context = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("Answer text", List.of(), "llama3.1:8b");

        // Claims with different formats: numbered and bullet points
        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenReturn("1. First numbered claim here\n- Second bullet claim here");
        when(ollamaClient.generate(any(LlmModel.class), contains("Does the following"), anyInt()))
                .thenReturn("yes");

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.claims()).hasSize(2);
    }

    @Test
    void shouldHandleMultipleChunksInContext() {
        Embedding embedding = new Embedding(new float[] { 0.1f }, "nomic-embed-text");
        Chunk chunk1 = new Chunk("chunk-1", "doc-1", "Chunk 1 content", 0, embedding);
        Chunk chunk2 = new Chunk("chunk-2", "doc-1", "Chunk 2 content", 1, embedding);
        Chunk chunk3 = new Chunk("chunk-3", "doc-1", "Chunk 3 content", 2, embedding);
        Chunk chunk4 = new Chunk("chunk-4", "doc-1", "Chunk 4 content", 3, embedding);
        RetrievalResult context = new RetrievalResult(List.of(chunk1, chunk2, chunk3, chunk4), "DEEP", 100L);

        Answer answer = new Answer("Answer text", List.of(), "llama3.1:8b");

        when(ollamaClient.generate(any(LlmModel.class), contains("Extract"), anyInt()))
                .thenReturn("1. A valid claim from the content");
        when(ollamaClient.generate(any(LlmModel.class), contains("Does the following"), anyInt()))
                .thenReturn("yes");

        VerificationResult result = verifier.verify(answer, context);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(VerificationStatus.GROUNDED);
    }
}
