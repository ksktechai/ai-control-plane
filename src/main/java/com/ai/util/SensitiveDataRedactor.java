package com.ai.common.util;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Redacts sensitive data from logs and responses.
 */
public final class SensitiveDataRedactor {
    private static final Set<String> SENSITIVE_FIELD_NAMES = Set.of(
        "password", "token", "secret", "apikey", "authorization", "cookie"
    );

    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
        "\\b(" + String.join("|", SENSITIVE_FIELD_NAMES) + ")\\b",
        Pattern.CASE_INSENSITIVE
    );

    private static final String REDACTED = "[REDACTED]";
    private static final int MAX_LOG_LENGTH = 10000;

    private SensitiveDataRedactor() {
        // Utility class
    }

    /**
     * Redacts sensitive fields from a JSON-like string.
     */
    public static String redact(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        String redacted = input;

        // Redact sensitive field values
        for (String field : SENSITIVE_FIELD_NAMES) {
            // Pattern to match: "field": "value" or field=value or field: multi word value
            // For colon separator, match everything to end of line or next comma/brace
            // For equals separator, match single word only
            Pattern colonPattern = Pattern.compile(
                "([\"']?" + field + "[\"']?\\s*:\\s*[\"']?)([^\"',}\\n]+)",
                Pattern.CASE_INSENSITIVE
            );
            redacted = colonPattern.matcher(redacted)
                .replaceAll("$1" + REDACTED);

            // For equals separator, match only single word
            Pattern equalsPattern = Pattern.compile(
                "([\"']?" + field + "[\"']?\\s*=\\s*[\"']?)([^\"',}\\s]+)",
                Pattern.CASE_INSENSITIVE
            );
            redacted = equalsPattern.matcher(redacted)
                .replaceAll("$1" + REDACTED);
        }

        return truncate(redacted);
    }

    /**
     * Truncates long strings to prevent log bloat.
     */
    public static String truncate(String input) {
        if (input == null || input.length() <= MAX_LOG_LENGTH) {
            return input;
        }
        return input.substring(0, MAX_LOG_LENGTH) + "... [TRUNCATED]";
    }

    /**
     * Checks if a field name is sensitive.
     */
    public static boolean isSensitiveField(String fieldName) {
        if (fieldName == null) {
            return false;
        }
        return SENSITIVE_PATTERN.matcher(fieldName).find();
    }
}
