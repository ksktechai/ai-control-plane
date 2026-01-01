package com.ai.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ChunkTest {

    private Embedding createTestEmbedding() {
        return new Embedding(new float[]{0.1f, 0.2f, 0.3f}, "nomic-embed-text");
    }

    @Test
    void shouldCreateValidChunk() {
        Embedding embedding = createTestEmbedding();
        Chunk chunk = new Chunk("chunk-1", "doc-1", "sample text", 0, embedding);

        assertThat(chunk.id()).isEqualTo("chunk-1");
        assertThat(chunk.documentId()).isEqualTo("doc-1");
        assertThat(chunk.text()).isEqualTo("sample text");
        assertThat(chunk.position()).isEqualTo(0);
        assertThat(chunk.embedding()).isEqualTo(embedding);
    }

    @Test
    void shouldRejectNullId() {
        Embedding embedding = createTestEmbedding();
        assertThatThrownBy(() -> new Chunk(null, "doc-1", "text", 0, embedding))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Chunk ID cannot be null or blank");
    }

    @Test
    void shouldRejectBlankId() {
        Embedding embedding = createTestEmbedding();
        assertThatThrownBy(() -> new Chunk("  ", "doc-1", "text", 0, embedding))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Chunk ID cannot be null or blank");
    }

    @Test
    void shouldRejectNullDocumentId() {
        Embedding embedding = createTestEmbedding();
        assertThatThrownBy(() -> new Chunk("chunk-1", null, "text", 0, embedding))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document ID cannot be null or blank");
    }

    @Test
    void shouldRejectBlankDocumentId() {
        Embedding embedding = createTestEmbedding();
        assertThatThrownBy(() -> new Chunk("chunk-1", "  ", "text", 0, embedding))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document ID cannot be null or blank");
    }

    @Test
    void shouldRejectNullText() {
        Embedding embedding = createTestEmbedding();
        assertThatThrownBy(() -> new Chunk("chunk-1", "doc-1", null, 0, embedding))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Chunk text cannot be null or blank");
    }

    @Test
    void shouldRejectBlankText() {
        Embedding embedding = createTestEmbedding();
        assertThatThrownBy(() -> new Chunk("chunk-1", "doc-1", "  ", 0, embedding))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Chunk text cannot be null or blank");
    }

    @Test
    void shouldRejectNegativePosition() {
        Embedding embedding = createTestEmbedding();
        assertThatThrownBy(() -> new Chunk("chunk-1", "doc-1", "text", -1, embedding))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Position cannot be negative");
    }

    @Test
    void shouldRejectNullEmbedding() {
        assertThatThrownBy(() -> new Chunk("chunk-1", "doc-1", "text", 0, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Embedding cannot be null");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Embedding embedding = createTestEmbedding();
        Embedding differentEmbedding = new Embedding(new float[]{0.4f}, "different");
        Chunk c1 = new Chunk("chunk-1", "doc-1", "text", 0, embedding);
        Chunk c2 = new Chunk("chunk-1", "doc-1", "text", 0, embedding);
        Chunk c3 = new Chunk("chunk-2", "doc-1", "text", 0, embedding);
        Chunk c4 = new Chunk("chunk-1", "doc-2", "text", 0, embedding);
        Chunk c5 = new Chunk("chunk-1", "doc-1", "different", 0, embedding);
        Chunk c6 = new Chunk("chunk-1", "doc-1", "text", 1, embedding);
        Chunk c7 = new Chunk("chunk-1", "doc-1", "text", 0, differentEmbedding);

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).isNotEqualTo(c3);
        assertThat(c1).isNotEqualTo(c4);
        assertThat(c1).isNotEqualTo(c5);
        assertThat(c1).isNotEqualTo(c6);
        assertThat(c1).isNotEqualTo(c7);
        assertThat(c1).isEqualTo(c1);
        assertThat(c1).isNotEqualTo(null);
        assertThat(c1).isNotEqualTo("not a Chunk");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Embedding embedding = createTestEmbedding();
        Chunk c1 = new Chunk("chunk-1", "doc-1", "text", 0, embedding);
        Chunk c2 = new Chunk("chunk-1", "doc-1", "text", 0, embedding);

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Embedding embedding = createTestEmbedding();
        Chunk chunk = new Chunk("chunk-1", "doc-1", "sample text", 0, embedding);

        assertThat(chunk.toString())
            .contains("Chunk")
            .contains("chunk-1")
            .contains("doc-1")
            .contains("position=0")
            .contains("textLength=11");
    }
}
