package com.ai.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Represents a user question to be answered by the system. */
public final class Question {
    private final String text;
    private final String correlationId;

    @JsonCreator
    public Question(
            @JsonProperty("text") String text,
            @JsonProperty("correlationId") String correlationId) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be null or blank");
        }
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("Correlation ID cannot be null or blank");
        }
        this.text = text;
        this.correlationId = correlationId;
    }

    @JsonGetter("text")
    public String text() {
        return text;
    }

    @JsonGetter("correlationId")
    public String correlationId() {
        return correlationId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Question other)) return false;
        return Objects.equals(text, other.text)
                && Objects.equals(correlationId, other.correlationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, correlationId);
    }

    @Override
    public String toString() {
        return "Question[text=" + text + ", correlationId=" + correlationId + "]";
    }
}
