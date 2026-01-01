package com.ai.control;

import com.ai.common.domain.*;
import com.ai.common.model.LlmModel;
import com.ai.common.model.RetrievalStrategy;
import com.ai.llm.OllamaClient;
import com.ai.rag.RetrievalService;
import com.ai.verifier.AnswerVerifier;
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
class ControlPlaneImplTest {

    @Mock
    private RetrievalService retrievalService;

    @Mock
    private OllamaClient ollamaClient;

    @Mock
    private AnswerVerifier answerVerifier;

    private ControlPlane controlPlane;

    @BeforeEach
    void setUp() {
        controlPlane = new ControlPlaneImpl(retrievalService, ollamaClient, answerVerifier);
    }

    @Test
    void shouldAnswerQuestionSuccessfully() {
        Question question = new Question("What is AI?", "corr-123");

        Embedding embedding = new Embedding(new float[]{0.1f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult retrievalResult = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("AI is artificial intelligence", List.of(), "phi3:mini");

        VerificationResult verification = new VerificationResult(
            VerificationStatus.GROUNDED,
            List.of(),
            0.95,
            "Fully grounded"
        );

        when(retrievalService.retrieve(anyString(), any(RetrievalStrategy.class)))
            .thenReturn(retrievalResult);
        when(ollamaClient.generate(any(LlmModel.class), anyString(), anyInt()))
            .thenReturn("AI is artificial intelligence");
        when(answerVerifier.verify(any(Answer.class), any(RetrievalResult.class)))
            .thenReturn(verification);

        AnswerResult result = controlPlane.answer(question);

        assertThat(result).isNotNull();
        assertThat(result.answer()).isNotNull();
        assertThat(result.verification()).isEqualTo(verification);
        assertThat(result.confidence()).isGreaterThanOrEqualTo(0.7);
    }

    @Test
    void shouldThrowExceptionForNullQuestion() {
        assertThatThrownBy(() -> controlPlane.answer(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Question cannot be null");
    }

    @Test
    void shouldRetryWithEscalatedModelWhenConfidenceLow() {
        Question question = new Question("What is AI?", "corr-123");

        Embedding embedding = new Embedding(new float[]{0.1f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult retrievalResult = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer1 = new Answer("Vague answer", List.of(), "phi3:mini");
        Answer answer2 = new Answer("Better answer", List.of(), "qwen2.5:7b");

        // First attempt returns low confidence
        VerificationResult lowConfidenceVerification = new VerificationResult(
            VerificationStatus.PARTIALLY_GROUNDED,
            List.of(),
            0.5,
            "Partially grounded"
        );

        // Second attempt returns high confidence
        VerificationResult highConfidenceVerification = new VerificationResult(
            VerificationStatus.GROUNDED,
            List.of(),
            0.95,
            "Fully grounded"
        );

        when(retrievalService.retrieve(anyString(), any(RetrievalStrategy.class)))
            .thenReturn(retrievalResult);
        when(ollamaClient.generate(any(LlmModel.class), anyString(), anyInt()))
            .thenReturn("Vague answer")
            .thenReturn("Better answer");
        when(answerVerifier.verify(any(Answer.class), any(RetrievalResult.class)))
            .thenReturn(lowConfidenceVerification)
            .thenReturn(highConfidenceVerification);

        AnswerResult result = controlPlane.answer(question);

        assertThat(result).isNotNull();
        assertThat(result.confidence()).isGreaterThanOrEqualTo(0.7);
    }

    @Test
    void shouldReturnLowConfidenceAnswerAfterMaxRetries() {
        Question question = new Question("What is AI?", "corr-123");

        Embedding embedding = new Embedding(new float[]{0.1f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult retrievalResult = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("Vague answer", List.of(), "phi3:mini");

        // Always return low confidence
        VerificationResult lowConfidenceVerification = new VerificationResult(
            VerificationStatus.UNGROUNDED,
            List.of(),
            0.2,
            "Ungrounded"
        );

        when(retrievalService.retrieve(anyString(), any(RetrievalStrategy.class)))
            .thenReturn(retrievalResult);
        when(ollamaClient.generate(any(LlmModel.class), anyString(), anyInt()))
            .thenReturn("Vague answer");
        when(answerVerifier.verify(any(Answer.class), any(RetrievalResult.class)))
            .thenReturn(lowConfidenceVerification);

        AnswerResult result = controlPlane.answer(question);

        assertThat(result).isNotNull();
        assertThat(result.confidence()).isLessThan(0.7);
    }

    @Test
    void shouldThrowExceptionAfterMaxRetriesOnError() {
        Question question = new Question("What is AI?", "corr-123");

        when(retrievalService.retrieve(anyString(), any(RetrievalStrategy.class)))
            .thenThrow(new RuntimeException("Retrieval failed"));

        assertThatThrownBy(() -> controlPlane.answer(question))
            .isInstanceOf(ControlPlaneException.class)
            .hasMessageContaining("Failed to generate answer after 2 attempts");
    }

    @Test
    void shouldCalculateConfidenceForPartiallyGrounded() {
        Question question = new Question("What is AI?", "corr-123");

        Embedding embedding = new Embedding(new float[]{0.1f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult retrievalResult = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("Partially correct answer", List.of(), "phi3:mini");

        VerificationResult partialVerification = new VerificationResult(
            VerificationStatus.PARTIALLY_GROUNDED,
            List.of(),
            0.9,  // groundingScore
            "Partially grounded"
        );

        when(retrievalService.retrieve(anyString(), any(RetrievalStrategy.class)))
            .thenReturn(retrievalResult);
        when(ollamaClient.generate(any(LlmModel.class), anyString(), anyInt()))
            .thenReturn("Partially correct answer");
        when(answerVerifier.verify(any(Answer.class), any(RetrievalResult.class)))
            .thenReturn(partialVerification);

        AnswerResult result = controlPlane.answer(question);

        assertThat(result).isNotNull();
        assertThat(result.confidence()).isEqualTo(0.9 * 0.8); // groundingScore * 0.8
    }

    @Test
    void shouldCalculateConfidenceForFailed() {
        Question question = new Question("What is AI?", "corr-123");

        Embedding embedding = new Embedding(new float[]{0.1f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "AI is artificial intelligence", 0, embedding);
        RetrievalResult retrievalResult = new RetrievalResult(List.of(chunk), "SIMPLE", 100L);

        Answer answer = new Answer("Failed answer", List.of(), "phi3:mini");

        VerificationResult failedVerification = new VerificationResult(
            VerificationStatus.FAILED,
            List.of(),
            0.0,
            "Verification failed"
        );

        when(retrievalService.retrieve(anyString(), any(RetrievalStrategy.class)))
            .thenReturn(retrievalResult);
        when(ollamaClient.generate(any(LlmModel.class), anyString(), anyInt()))
            .thenReturn("Failed answer");
        when(answerVerifier.verify(any(Answer.class), any(RetrievalResult.class)))
            .thenReturn(failedVerification);

        AnswerResult result = controlPlane.answer(question);

        assertThat(result).isNotNull();
        assertThat(result.confidence()).isEqualTo(0.1);
    }
}
