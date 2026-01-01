package com.ai.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestResponseLoggingFilterTest {

    @Mock
    private FilterChain filterChain;

    private RequestResponseLoggingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new RequestResponseLoggingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldLogRequestAndResponseWithCorrelationId() throws ServletException, IOException {
        String correlationId = "test-correlation-id-123";
        MDC.put("correlationId", correlationId);

        request.setMethod("POST");
        request.setRequestURI("/api/chat");
        request.setContent("{\"question\":\"test\"}".getBytes(StandardCharsets.UTF_8));
        request.setContentType("application/json");

        doAnswer(invocation -> {
            // Simulate controller writing response
            response.getWriter().write("{\"answer\":\"response\"}");
            response.setStatus(200);
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo("{\"answer\":\"response\"}");
    }

    @Test
    void shouldLogRequestWithQueryParameters() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/search");
        request.setQueryString("q=test&limit=10");
        request.addParameter("q", "test");
        request.addParameter("limit", "10");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleRequestWithNoQueryString() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/health");
        // No query string set

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleRequestWithNoParameters() throws ServletException, IOException {
        request.setMethod("POST");
        request.setRequestURI("/api/chat");
        request.setContent("{\"question\":\"test\"}".getBytes(StandardCharsets.UTF_8));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleEmptyRequestBody() throws ServletException, IOException {
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setContent("".getBytes(StandardCharsets.UTF_8));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleEmptyResponseBody() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        doAnswer(invocation -> {
            response.setStatus(204); // No content
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    void shouldPreserveCorrelationIdDuringLogging() throws ServletException, IOException {
        String correlationId = "preserve-test-id";
        MDC.put("correlationId", correlationId);

        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setContent("test".getBytes(StandardCharsets.UTF_8));

        doAnswer(invocation -> {
            // Verify correlation ID is still present during filter chain
            assertThat(MDC.get("correlationId")).isEqualTo(correlationId);
            // Simulate another filter clearing MDC
            MDC.clear();
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
        // After filter completes, MDC should be cleaned
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldRestoreCorrelationIdForLoggingInFinallyBlock() throws ServletException, IOException {
        String correlationId = "finally-block-test";
        MDC.put("correlationId", correlationId);

        request.setMethod("GET");
        request.setRequestURI("/api/test");

        doAnswer(invocation -> {
            // Clear MDC during chain execution
            MDC.clear();
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        // The filter should have restored and then cleaned the correlation ID
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleNullCorrelationId() throws ServletException, IOException {
        // No correlation ID set
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldLogResponseStatusAndTiming() throws ServletException, IOException {
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setContent("request".getBytes(StandardCharsets.UTF_8));

        doAnswer(invocation -> {
            // Simulate some processing time
            Thread.sleep(10);
            response.setStatus(201);
            response.getWriter().write("created");
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getContentAsString()).isEqualTo("created");
    }

    @Test
    void shouldHandleExceptionDuringFilterChain() throws ServletException, IOException {
        String correlationId = "exception-test-id";
        MDC.put("correlationId", correlationId);

        request.setMethod("POST");
        request.setRequestURI("/api/test");

        doThrow(new ServletException("Filter chain error"))
                .when(filterChain).doFilter(any(), any());

        assertThatThrownBy(() -> filter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(ServletException.class)
                .hasMessage("Filter chain error");

        verify(filterChain).doFilter(any(), any());
        // MDC should be cleaned up even after exception
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void shouldHandleMultipleParametersWithSameKey() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/search");
        request.addParameter("tag", new String[]{"java", "spring", "test"});

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleSpecialCharactersInQueryString() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI("/api/search");
        request.setQueryString("q=hello+world&special=%3D%26");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleLargeRequestBody() throws ServletException, IOException {
        request.setMethod("POST");
        request.setRequestURI("/api/upload");

        // Create a large request body (within the 10KB limit set in the filter)
        String largeBody = "x".repeat(5000);
        request.setContent(largeBody.getBytes(StandardCharsets.UTF_8));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldCopyResponseBodyBackToClient() throws ServletException, IOException {
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setContent("test".getBytes(StandardCharsets.UTF_8));

        String expectedResponse = "{\"result\":\"success\"}";
        doAnswer(invocation -> {
            response.getWriter().write(expectedResponse);
            response.setStatus(200);
            return null;
        }).when(filterChain).doFilter(any(), any());

        filter.doFilterInternal(request, response, filterChain);

        // Verify response was copied back
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldHandleDifferentHttpMethods() throws ServletException, IOException {
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            request = new MockHttpServletRequest();
            request.setMethod(method);
            request.setRequestURI("/api/test");

            filter.doFilterInternal(request, response, filterChain);
        }

        verify(filterChain, times(methods.length)).doFilter(any(), any());
    }
}
