package com.ai.common.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AnswerResultTest {

    private Answer createTestAnswer() {
        return new Answer("AI is artificial intelligence", List.of(), "llama3.1:8b");
    }

    private VerificationResult createTestVerification() {
        return new VerificationResult(
            VerificationStatus.GROUNDED,
            List.of(),
            0.95,
            "All claims verified"
        );
    }

    @Test
    void shouldCreateValidAnswerResult() {
        Answer answer = createTestAnswer();
        VerificationResult verification = createTestVerification();

        AnswerResult result = new AnswerResult(answer, verification, 0.9, "SIMPLE");

        assertThat(result.answer()).isEqualTo(answer);
        assertThat(result.verification()).isEqualTo(verification);
        assertThat(result.confidence()).isEqualTo(0.9);
        assertThat(result.retrievalStrategy()).isEqualTo("SIMPLE");
    }

    @Test
    void shouldRejectNullAnswer() {
        VerificationResult verification = createTestVerification();

        assertThatThrownBy(() -> new AnswerResult(null, verification, 0.9, "SIMPLE"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Answer cannot be null");
    }

    @Test
    void shouldRejectNullVerification() {
        Answer answer = createTestAnswer();

        assertThatThrownBy(() -> new AnswerResult(answer, null, 0.9, "SIMPLE"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Verification cannot be null");
    }

    @Test
    void shouldRejectNegativeConfidence() {
        Answer answer = createTestAnswer();
        VerificationResult verification = createTestVerification();

        assertThatThrownBy(() -> new AnswerResult(answer, verification, -0.1, "SIMPLE"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Confidence must be between 0.0 and 1.0");
    }

    @Test
    void shouldRejectConfidenceAboveOne() {
        Answer answer = createTestAnswer();
        VerificationResult verification = createTestVerification();

        assertThatThrownBy(() -> new AnswerResult(answer, verification, 1.1, "SIMPLE"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Confidence must be between 0.0 and 1.0");
    }

    @Test
    void shouldRejectNullRetrievalStrategy() {
        Answer answer = createTestAnswer();
        VerificationResult verification = createTestVerification();

        assertThatThrownBy(() -> new AnswerResult(answer, verification, 0.9, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Retrieval strategy cannot be null or blank");
    }

    @Test
    void shouldRejectBlankRetrievalStrategy() {
        Answer answer = createTestAnswer();
        VerificationResult verification = createTestVerification();

        assertThatThrownBy(() -> new AnswerResult(answer, verification, 0.9, "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Retrieval strategy cannot be null or blank");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Answer answer = createTestAnswer();
        Answer differentAnswer = new Answer("Different", List.of(), "model");
        VerificationResult verification = createTestVerification();
        VerificationResult differentVerification = new VerificationResult(
            VerificationStatus.UNGROUNDED, List.of(), 0.5, "Different"
        );

        AnswerResult r1 = new AnswerResult(answer, verification, 0.9, "SIMPLE");
        AnswerResult r2 = new AnswerResult(answer, verification, 0.9, "SIMPLE");
        AnswerResult r3 = new AnswerResult(answer, verification, 0.8, "SIMPLE");
        AnswerResult r4 = new AnswerResult(differentAnswer, verification, 0.9, "SIMPLE");
        AnswerResult r5 = new AnswerResult(answer, differentVerification, 0.9, "SIMPLE");
        AnswerResult r6 = new AnswerResult(answer, verification, 0.9, "DIFFERENT");

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
        assertThat(r1).isNotEqualTo(r4);
        assertThat(r1).isNotEqualTo(r5);
        assertThat(r1).isNotEqualTo(r6);
        assertThat(r1).isEqualTo(r1);
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo("not an AnswerResult");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Answer answer = createTestAnswer();
        VerificationResult verification = createTestVerification();

        AnswerResult r1 = new AnswerResult(answer, verification, 0.9, "SIMPLE");
        AnswerResult r2 = new AnswerResult(answer, verification, 0.9, "SIMPLE");

        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Answer answer = createTestAnswer();
        VerificationResult verification = createTestVerification();
        AnswerResult result = new AnswerResult(answer, verification, 0.9, "SIMPLE");

        assertThat(result.toString())
            .contains("AnswerResult")
            .contains("0.9")
            .contains("SIMPLE")
            .contains("GROUNDED");
    }
}
