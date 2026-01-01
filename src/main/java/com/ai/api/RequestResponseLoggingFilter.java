package com.ai.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Filter that logs every API request and response body. Wrapping requests/responses is necessary to
 * read the stream nicely. This filter runs after RequestCorrelationFilter (Order = 2) to ensure
 * correlation ID is in MDC.
 */
@Component
@Order(2)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    /**
     * Filter that logs every API request and response body. Wrapping requests/responses is
     * necessary to read the body multiple times (for logging) without consuming it for the actual
     * endpoint. Limits cache to 10MB to avoid memory issues with huge uploads (though we prefer
     * small chat messages)
     *
     * @param request The request to process
     * @param response The response associated with the request
     * @param filterChain Provides access to the next filter in the chain for this filter to pass
     *     the request and response to for further processing
     * @throws ServletException Thrown if a servlet-specific error occurs during the processing of
     *     the request or response
     * @throws IOException Thrown if an I/O error occurs during the processing of the request or
     *     response
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Use cached wrappers so we can read the body multiple times (for logging)
        // without consuming it for the actual endpoint.
        // Limit cache to 10MB to avoid memory issues with huge uploads (though we
        // prefer small chat messages)
        // Limit cache to 10MB to avoid memory issues with huge uploads
        ContentCachingRequestWrapper reqWrapper = new ContentCachingRequestWrapper(request, 10000);
        ContentCachingResponseWrapper respWrapper = new ContentCachingResponseWrapper(response);

        // Save correlation ID for logging (in case MDC is cleared before finally block)
        String correlationId = org.slf4j.MDC.get("correlationId");

        logger.info(
                "API INCOMING [{} {}] query={} params={}",
                request.getMethod(),
                request.getRequestURI(),
                decodeQueryString(request.getQueryString()),
                formatParams(request));

        long startTime = System.currentTimeMillis();
        try {
            // Proceed with the chain (this executes the controller)
            filterChain.doFilter(reqWrapper, respWrapper);
        } finally {
            // Restore correlation ID for logging
            if (correlationId != null) {
                org.slf4j.MDC.put("correlationId", correlationId);
            }

            long duration = System.currentTimeMillis() - startTime;

            // Log Request
            String requestBody =
                    new String(reqWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            // Replace newlines to keep logs clean-ish? Or keep them for readability. Let's
            // simple trim.
            logger.info(
                    "API REQUEST [{} {}] Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    requestBody);

            // Log Response
            String responseBody =
                    new String(respWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            logger.info(
                    "API RESPONSE [{} {}] Status: {} Time: {} ms Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    responseBody);

            // IMPORTANT: Copy content back to the original response so the client receives
            // it!
            respWrapper.copyBodyToResponse();

            // Clean up MDC after logging
            if (correlationId != null) {
                org.slf4j.MDC.remove("correlationId");
            }
        }
    }

    /**
     * Decode a query string, handling null, and decoding with UTF-8.
     *
     * @param query The query string to decode
     * @return The decoded query string, or an empty string if the input is null
     */
    private String decodeQueryString(String query) {
        if (query == null) return "";
        return URLDecoder.decode(query, StandardCharsets.UTF_8);
    }

    /**
     * Format the request parameters into a string representation.
     *
     * @param request The request to format parameters for
     * @return A string representation of the request parameters
     */
    private String formatParams(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        if (params.isEmpty()) return "{}";

        return params.entrySet().stream()
                .map(e -> e.getKey() + "=" + String.join(",", e.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
