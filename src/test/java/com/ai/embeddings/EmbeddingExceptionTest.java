package com.ai.embeddings;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmbeddingExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        EmbeddingException exception = new EmbeddingException("Test message");

        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        EmbeddingException exception = new EmbeddingException("Test message", cause);

        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
