package com.ai.api;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add a unique correlation ID to each request for logging and tracing purposes.
 * This filter runs first (Order = 1) to ensure the correlation ID is available for all subsequent filters.
 */
@Component
@Order(1)
public class RequestCorrelationFilter implements Filter {

    public static final String HEADER = "X-Correlation-Id";

    /**
     * Filter to add a unique correlation ID to each request for logging and tracing purposes.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this filter to pass the request and response
     *                 to for further processing
     * @throws IOException      Thrown if an I/O error occurs during the processing of the request or response
     * @throws ServletException Thrown if a servlet-specific error occurs during the processing of the request or response
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String cid = req.getHeader(HEADER);
        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
        MDC.put("correlationId", cid);
        try {
            chain.doFilter(request, response);
        } finally {
            // Clear MDC after the request completes to prevent leaking to other threads
            MDC.clear();
        }
    }
}
