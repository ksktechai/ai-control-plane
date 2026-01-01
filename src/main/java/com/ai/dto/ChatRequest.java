package com.ai.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Request DTO for chat endpoint.
 */
public final class ChatRequest {
    private final String question;

    @JsonCreator
    public ChatRequest(@JsonProperty("question") String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or blank");
        }
        this.question = question;
    }

    @JsonGetter("question")
    public String question() {
        return question;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChatRequest other)) return false;
        return Objects.equals(question, other.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question);
    }

    @Override
    public String toString() {
        return "ChatRequest[question=" + question + "]";
    }
}
