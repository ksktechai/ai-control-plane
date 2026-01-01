package com.ai.control;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ControlPlaneExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        ControlPlaneException exception = new ControlPlaneException("Test message");

        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        ControlPlaneException exception = new ControlPlaneException("Test message", cause);

        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
