package com.ai.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a source document in the knowledge base.
 */
public final class Document {
    private final String id;
    private final String title;
    private final String content;
    private final String source;
    private final Instant createdAt;

    @JsonCreator
    public Document(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("source") String source,
            @JsonProperty("createdAt") Instant createdAt) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Document ID cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Document title cannot be null or blank");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Document content cannot be null or blank");
        }
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Document source cannot be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created timestamp cannot be null");
        }
        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.createdAt = createdAt;
    }

    @JsonGetter("id")
    public String id() {
        return id;
    }

    @JsonGetter("title")
    public String title() {
        return title;
    }

    @JsonGetter("content")
    public String content() {
        return content;
    }

    @JsonGetter("source")
    public String source() {
        return source;
    }

    @JsonGetter("createdAt")
    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Document other)) return false;
        return Objects.equals(id, other.id) &&
               Objects.equals(title, other.title) &&
               Objects.equals(content, other.content) &&
               Objects.equals(source, other.source) &&
               Objects.equals(createdAt, other.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, source, createdAt);
    }

    @Override
    public String toString() {
        return "Document[id=" + id +
               ", title=" + title +
               ", source=" + source + "]";
    }
}
