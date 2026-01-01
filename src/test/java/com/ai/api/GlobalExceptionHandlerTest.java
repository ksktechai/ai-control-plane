package com.ai.api;

import com.ai.util.CorrelationIdHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        CorrelationIdHolder.set("test-correlation-id");
    }

    @AfterEach
    void tearDown() {
        CorrelationIdHolder.clearAll();
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ResponseEntity<?> response = exceptionHandler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldHandleIllegalArgumentExceptionWithoutCorrelationId() {
        CorrelationIdHolder.clearAll();
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ResponseEntity<?> response = exceptionHandler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<?> response = exceptionHandler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldHandleGenericExceptionWithoutCorrelationId() {
        CorrelationIdHolder.clearAll();
        Exception ex = new NullPointerException("Null value");

        ResponseEntity<?> response = exceptionHandler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldIncludeCorrelationIdInResponse() {
        String correlationId = "my-correlation-id";
        CorrelationIdHolder.set(correlationId);

        IllegalArgumentException ex = new IllegalArgumentException("Test error");

        ResponseEntity<?> response = exceptionHandler.handleIllegalArgument(ex);

        assertThat(response.getBody().toString()).contains(correlationId);
    }
}
