package com.ai.common.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QuestionTest {

    @Test
    void shouldCreateValidQuestion() {
        Question question = new Question("What is AI?", "corr-123");

        assertThat(question.text()).isEqualTo("What is AI?");
        assertThat(question.correlationId()).isEqualTo("corr-123");
    }

    @Test
    void shouldRejectNullText() {
        assertThatThrownBy(() -> new Question(null, "corr-123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Question text cannot be null or blank");
    }

    @Test
    void shouldRejectBlankText() {
        assertThatThrownBy(() -> new Question("  ", "corr-123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Question text cannot be null or blank");
    }

    @Test
    void shouldRejectNullCorrelationId() {
        assertThatThrownBy(() -> new Question("What is AI?", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Correlation ID cannot be null or blank");
    }

    @Test
    void shouldRejectBlankCorrelationId() {
        assertThatThrownBy(() -> new Question("What is AI?", "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Correlation ID cannot be null or blank");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Question q1 = new Question("What is AI?", "corr-123");
        Question q2 = new Question("What is AI?", "corr-123");
        Question q3 = new Question("Different question", "corr-123");
        Question q4 = new Question("What is AI?", "corr-456");

        assertThat(q1).isEqualTo(q2);
        assertThat(q1).isNotEqualTo(q3);
        assertThat(q1).isNotEqualTo(q4);
        assertThat(q1).isNotEqualTo(null);
        assertThat(q1).isNotEqualTo("string");
        assertThat(q1).isEqualTo(q1);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Question q1 = new Question("What is AI?", "corr-123");
        Question q2 = new Question("What is AI?", "corr-123");

        assertThat(q1.hashCode()).isEqualTo(q2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Question question = new Question("What is AI?", "corr-123");

        assertThat(question.toString())
            .contains("Question")
            .contains("What is AI?")
            .contains("corr-123");
    }
}
