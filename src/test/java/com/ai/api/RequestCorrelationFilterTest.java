package com.ai.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestCorrelationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RequestCorrelationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RequestCorrelationFilter();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldGenerateNewCorrelationIdWhenHeaderNotPresent() throws ServletException, IOException {
        when(request.getHeader(RequestCorrelationFilter.HEADER)).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // MDC should be cleared after the chain
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldGenerateNewCorrelationIdWhenHeaderIsBlank() throws ServletException, IOException {
        when(request.getHeader(RequestCorrelationFilter.HEADER)).thenReturn("  ");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldUseExistingCorrelationIdFromHeader() throws ServletException, IOException {
        String existingId = "existing-correlation-id-123";
        when(request.getHeader(RequestCorrelationFilter.HEADER)).thenReturn(existingId);

        doAnswer(invocation -> {
            // Verify MDC is set during filter chain execution
            assertThat(MDC.get("correlationId")).isEqualTo(existingId);
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // MDC should be cleared after the chain
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldSetMdcDuringFilterChainExecution() throws ServletException, IOException {
        when(request.getHeader(RequestCorrelationFilter.HEADER)).thenReturn(null);

        doAnswer(invocation -> {
            // Verify MDC is set during filter chain execution
            String correlationId = MDC.get("correlationId");
            assertThat(correlationId).isNotNull();
            assertThat(correlationId).isNotBlank();
            // UUID format validation
            assertThat(correlationId).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldClearMdcAfterFilterChainCompletion() throws ServletException, IOException {
        when(request.getHeader(RequestCorrelationFilter.HEADER)).thenReturn("test-id");

        filter.doFilter(request, response, filterChain);

        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldClearMdcEvenWhenFilterChainThrowsException() throws ServletException, IOException {
        when(request.getHeader(RequestCorrelationFilter.HEADER)).thenReturn("test-id");
        doThrow(new ServletException("Test exception")).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
                .isInstanceOf(ServletException.class)
                .hasMessage("Test exception");

        // MDC should still be cleared even when exception is thrown
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldClearMdcWhenIOExceptionOccurs() throws ServletException, IOException {
        when(request.getHeader(RequestCorrelationFilter.HEADER)).thenReturn("test-id");
        doThrow(new IOException("IO error")).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
                .isInstanceOf(IOException.class)
                .hasMessage("IO error");

        // MDC should still be cleared
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldHaveCorrectHeaderConstant() {
        assertThat(RequestCorrelationFilter.HEADER).isEqualTo("X-Correlation-Id");
    }
}
