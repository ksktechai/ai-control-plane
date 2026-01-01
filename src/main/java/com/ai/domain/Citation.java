package com.ai.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a citation referencing a source document chunk.
 */
public final class Citation {
    private final String chunkId;
    private final String documentId;
    private final String text;
    private final double relevanceScore;

    @JsonCreator
    public Citation(
            @JsonProperty("chunkId") String chunkId,
            @JsonProperty("documentId") String documentId,
            @JsonProperty("text") String text,
            @JsonProperty("relevanceScore") double relevanceScore) {
        if (chunkId == null || chunkId.isBlank()) {
            throw new IllegalArgumentException("Chunk ID cannot be null or blank");
        }
        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException("Document ID cannot be null or blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Citation text cannot be null or blank");
        }
        if (relevanceScore < 0.0 || relevanceScore > 1.0) {
            throw new IllegalArgumentException("Relevance score must be between 0.0 and 1.0");
        }
        this.chunkId = chunkId;
        this.documentId = documentId;
        this.text = text;
        this.relevanceScore = relevanceScore;
    }

    @JsonGetter("chunkId")
    public String chunkId() {
        return chunkId;
    }

    @JsonGetter("documentId")
    public String documentId() {
        return documentId;
    }

    @JsonGetter("text")
    public String text() {
        return text;
    }

    @JsonGetter("relevanceScore")
    public double relevanceScore() {
        return relevanceScore;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Citation other)) return false;
        return Objects.equals(chunkId, other.chunkId) &&
               Objects.equals(documentId, other.documentId) &&
               Objects.equals(text, other.text) &&
               Double.compare(relevanceScore, other.relevanceScore) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkId, documentId, text, relevanceScore);
    }

    @Override
    public String toString() {
        return "Citation[chunkId=" + chunkId +
               ", documentId=" + documentId +
               ", relevanceScore=" + relevanceScore + "]";
    }
}
