package com.ai.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a factual claim extracted from an answer.
 */
public final class Claim {
    private final String text;
    private final boolean isGrounded;
    private final String supportingChunkId;

    @JsonCreator
    public Claim(
            @JsonProperty("text") String text,
            @JsonProperty("isGrounded") boolean isGrounded,
            @JsonProperty("supportingChunkId") String supportingChunkId) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Claim text cannot be null or blank");
        }
        this.text = text;
        this.isGrounded = isGrounded;
        this.supportingChunkId = supportingChunkId;
    }

    @JsonGetter("text")
    public String text() {
        return text;
    }

    @JsonGetter("isGrounded")
    public boolean isGrounded() {
        return isGrounded;
    }

    @JsonGetter("supportingChunkId")
    public String supportingChunkId() {
        return supportingChunkId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Claim other)) return false;
        return Objects.equals(text, other.text) &&
               isGrounded == other.isGrounded &&
               Objects.equals(supportingChunkId, other.supportingChunkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, isGrounded, supportingChunkId);
    }

    @Override
    public String toString() {
        return "Claim[text=" + text +
               ", isGrounded=" + isGrounded +
               ", supportingChunkId=" + supportingChunkId + "]";
    }
}
