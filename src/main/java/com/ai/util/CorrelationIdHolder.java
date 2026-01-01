package com.ai.common.util;

import org.apache.logging.log4j.ThreadContext;
import java.util.UUID;

/**
 * Manages correlation IDs using Log4j2's ThreadContext (MDC).
 */
public final class CorrelationIdHolder {
    private static final String CORRELATION_ID_KEY = "correlationId";

    private CorrelationIdHolder() {
        // Utility class
    }

    /**
     * Sets the correlation ID in the thread context.
     */
    public static void set(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("Correlation ID cannot be null or blank");
        }
        ThreadContext.put(CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Gets the correlation ID from the thread context.
     * Returns null if not set.
     */
    public static String get() {
        return ThreadContext.get(CORRELATION_ID_KEY);
    }

    /**
     * Generates a new correlation ID and sets it in the thread context.
     * Returns the generated ID.
     */
    public static String generate() {
        String correlationId = UUID.randomUUID().toString();
        set(correlationId);
        return correlationId;
    }

    /**
     * Gets the current correlation ID, or generates a new one if not set.
     */
    public static String getOrGenerate() {
        String existing = get();
        return existing != null ? existing : generate();
    }

    /**
     * Clears the correlation ID from the thread context.
     */
    public static void clear() {
        ThreadContext.remove(CORRELATION_ID_KEY);
    }

    /**
     * Clears all MDC data.
     */
    public static void clearAll() {
        ThreadContext.clearAll();
    }
}
