package com.ai.verifier;

import com.ai.common.domain.*;
import com.ai.common.model.LlmModel;
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
        Embedding embedding = new Embedding(new float[]{0.1f}, "nomic-embed-text");
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
        Embedding embedding = new Embedding(new float[]{0.1f}, "nomic-embed-text");
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
}
