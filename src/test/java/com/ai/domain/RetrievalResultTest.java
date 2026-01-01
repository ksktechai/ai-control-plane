package com.ai.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RetrievalResultTest {

    private Chunk createTestChunk() {
        Embedding embedding = new Embedding(new float[] {0.1f, 0.2f}, "nomic-embed-text");
        return new Chunk("chunk-1", "doc-1", "text", 0, embedding);
    }

    @Test
    void shouldCreateValidRetrievalResult() {
        List<Chunk> chunks = List.of(createTestChunk());
        RetrievalResult result = new RetrievalResult(chunks, "SIMPLE", 100L);

        assertThat(result.chunks()).hasSize(1);
        assertThat(result.strategy()).isEqualTo("SIMPLE");
        assertThat(result.durationMs()).isEqualTo(100L);
    }

    @Test
    void shouldCreateResultWithEmptyChunks() {
        RetrievalResult result = new RetrievalResult(List.of(), "SIMPLE", 100L);

        assertThat(result.chunks()).isEmpty();
    }

    @Test
    void shouldRejectNullChunks() {
        assertThatThrownBy(() -> new RetrievalResult(null, "SIMPLE", 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chunks cannot be null");
    }

    @Test
    void shouldRejectNullStrategy() {
        assertThatThrownBy(() -> new RetrievalResult(List.of(), null, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Strategy cannot be null or blank");
    }

    @Test
    void shouldRejectBlankStrategy() {
        assertThatThrownBy(() -> new RetrievalResult(List.of(), "  ", 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Strategy cannot be null or blank");
    }

    @Test
    void shouldRejectNegativeDuration() {
        assertThatThrownBy(() -> new RetrievalResult(List.of(), "SIMPLE", -1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duration cannot be negative");
    }

    @Test
    void shouldReturnImmutableChunks() {
        List<Chunk> chunks = new ArrayList<>();
        chunks.add(createTestChunk());
        RetrievalResult result = new RetrievalResult(chunks, "SIMPLE", 100L);

        assertThatThrownBy(() -> result.chunks().add(createTestChunk()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        List<Chunk> chunks = List.of(createTestChunk());
        Embedding differentEmbedding = new Embedding(new float[] {0.9f}, "different");
        Chunk differentChunk = new Chunk("chunk-2", "doc-2", "different", 1, differentEmbedding);
        List<Chunk> differentChunks = List.of(differentChunk);

        RetrievalResult r1 = new RetrievalResult(chunks, "SIMPLE", 100L);
        RetrievalResult r2 = new RetrievalResult(chunks, "SIMPLE", 100L);
        RetrievalResult r3 = new RetrievalResult(chunks, "DEEP", 100L);
        RetrievalResult r4 = new RetrievalResult(chunks, "SIMPLE", 200L);
        RetrievalResult r5 = new RetrievalResult(differentChunks, "SIMPLE", 100L);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).isNotEqualTo(r3);
        assertThat(r1).isNotEqualTo(r4);
        assertThat(r1).isNotEqualTo(r5);
        assertThat(r1).isEqualTo(r1);
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo("not a RetrievalResult");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        List<Chunk> chunks = List.of(createTestChunk());
        RetrievalResult r1 = new RetrievalResult(chunks, "SIMPLE", 100L);
        RetrievalResult r2 = new RetrievalResult(chunks, "SIMPLE", 100L);

        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        List<Chunk> chunks = List.of(createTestChunk());
        RetrievalResult result = new RetrievalResult(chunks, "SIMPLE", 100L);

        assertThat(result.toString())
                .contains("RetrievalResult")
                .contains("chunkCount=1")
                .contains("SIMPLE")
                .contains("durationMs=100");
    }
}
