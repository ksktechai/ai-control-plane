package com.ai.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a text chunk from a document with its embedding.
 */
public final class Chunk {
    private final String id;
    private final String documentId;
    private final String text;
    private final int position;
    private final Embedding embedding;

    @JsonCreator
    public Chunk(
            @JsonProperty("id") String id,
            @JsonProperty("documentId") String documentId,
            @JsonProperty("text") String text,
            @JsonProperty("position") int position,
            @JsonProperty("embedding") Embedding embedding) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Chunk ID cannot be null or blank");
        }
        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException("Document ID cannot be null or blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Chunk text cannot be null or blank");
        }
        if (position < 0) {
            throw new IllegalArgumentException("Position cannot be negative");
        }
        if (embedding == null) {
            throw new IllegalArgumentException("Embedding cannot be null");
        }
        this.id = id;
        this.documentId = documentId;
        this.text = text;
        this.position = position;
        this.embedding = embedding;
    }

    @JsonGetter("id")
    public String id() {
        return id;
    }

    @JsonGetter("documentId")
    public String documentId() {
        return documentId;
    }

    @JsonGetter("text")
    public String text() {
        return text;
    }

    @JsonGetter("position")
    public int position() {
        return position;
    }

    @JsonGetter("embedding")
    public Embedding embedding() {
        return embedding;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Chunk other)) return false;
        return Objects.equals(id, other.id) &&
               Objects.equals(documentId, other.documentId) &&
               Objects.equals(text, other.text) &&
               position == other.position &&
               Objects.equals(embedding, other.embedding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documentId, text, position, embedding);
    }

    @Override
    public String toString() {
        return "Chunk[id=" + id +
               ", documentId=" + documentId +
               ", position=" + position +
               ", textLength=" + text.length() + "]";
    }
}
