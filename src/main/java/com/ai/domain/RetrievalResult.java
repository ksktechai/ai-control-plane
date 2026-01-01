package com.ai.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

/** Represents the result of a RAG retrieval operation. */
public final class RetrievalResult {
    private final List<Chunk> chunks;
    private final String strategy;
    private final long durationMs;

    @JsonCreator
    public RetrievalResult(
            @JsonProperty("chunks") List<Chunk> chunks,
            @JsonProperty("strategy") String strategy,
            @JsonProperty("durationMs") long durationMs) {
        if (chunks == null) {
            throw new IllegalArgumentException("Chunks cannot be null");
        }
        if (strategy == null || strategy.isBlank()) {
            throw new IllegalArgumentException("Strategy cannot be null or blank");
        }
        if (durationMs < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        this.chunks = List.copyOf(chunks);
        this.strategy = strategy;
        this.durationMs = durationMs;
    }

    @JsonGetter("chunks")
    public List<Chunk> chunks() {
        return chunks;
    }

    @JsonGetter("strategy")
    public String strategy() {
        return strategy;
    }

    @JsonGetter("durationMs")
    public long durationMs() {
        return durationMs;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RetrievalResult other)) return false;
        return Objects.equals(chunks, other.chunks)
                && Objects.equals(strategy, other.strategy)
                && durationMs == other.durationMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunks, strategy, durationMs);
    }

    @Override
    public String toString() {
        return "RetrievalResult[chunkCount="
                + chunks.size()
                + ", strategy="
                + strategy
                + ", durationMs="
                + durationMs
                + "]";
    }
}
