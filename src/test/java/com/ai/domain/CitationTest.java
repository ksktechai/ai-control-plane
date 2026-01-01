package com.ai.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CitationTest {

    @Test
    void shouldCreateValidCitation() {
        Citation citation = new Citation("chunk-1", "doc-1", "sample text", 0.95);

        assertThat(citation.chunkId()).isEqualTo("chunk-1");
        assertThat(citation.documentId()).isEqualTo("doc-1");
        assertThat(citation.text()).isEqualTo("sample text");
        assertThat(citation.relevanceScore()).isEqualTo(0.95);
    }

    @Test
    void shouldRejectNullChunkId() {
        assertThatThrownBy(() -> new Citation(null, "doc-1", "text", 0.95))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Chunk ID cannot be null or blank");
    }

    @Test
    void shouldRejectBlankChunkId() {
        assertThatThrownBy(() -> new Citation("  ", "doc-1", "text", 0.95))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Chunk ID cannot be null or blank");
    }

    @Test
    void shouldRejectNullDocumentId() {
        assertThatThrownBy(() -> new Citation("chunk-1", null, "text", 0.95))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document ID cannot be null or blank");
    }

    @Test
    void shouldRejectBlankDocumentId() {
        assertThatThrownBy(() -> new Citation("chunk-1", "  ", "text", 0.95))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document ID cannot be null or blank");
    }

    @Test
    void shouldRejectNullText() {
        assertThatThrownBy(() -> new Citation("chunk-1", "doc-1", null, 0.95))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Citation text cannot be null or blank");
    }

    @Test
    void shouldRejectBlankText() {
        assertThatThrownBy(() -> new Citation("chunk-1", "doc-1", "  ", 0.95))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Citation text cannot be null or blank");
    }

    @Test
    void shouldRejectNegativeRelevanceScore() {
        assertThatThrownBy(() -> new Citation("chunk-1", "doc-1", "text", -0.1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Relevance score must be between 0.0 and 1.0");
    }

    @Test
    void shouldRejectRelevanceScoreAboveOne() {
        assertThatThrownBy(() -> new Citation("chunk-1", "doc-1", "text", 1.1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Relevance score must be between 0.0 and 1.0");
    }

    @Test
    void shouldAcceptBoundaryRelevanceScores() {
        Citation c1 = new Citation("chunk-1", "doc-1", "text", 0.0);
        Citation c2 = new Citation("chunk-1", "doc-1", "text", 1.0);

        assertThat(c1.relevanceScore()).isEqualTo(0.0);
        assertThat(c2.relevanceScore()).isEqualTo(1.0);
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Citation c1 = new Citation("chunk-1", "doc-1", "text", 0.95);
        Citation c2 = new Citation("chunk-1", "doc-1", "text", 0.95);
        Citation c3 = new Citation("chunk-2", "doc-1", "text", 0.95);
        Citation c4 = new Citation("chunk-1", "doc-2", "text", 0.95);
        Citation c5 = new Citation("chunk-1", "doc-1", "different", 0.95);
        Citation c6 = new Citation("chunk-1", "doc-1", "text", 0.85);

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).isNotEqualTo(c3);
        assertThat(c1).isNotEqualTo(c4);
        assertThat(c1).isNotEqualTo(c5);
        assertThat(c1).isNotEqualTo(c6);
        assertThat(c1).isNotEqualTo(null);
        assertThat(c1).isNotEqualTo("not a Citation");
        assertThat(c1).isEqualTo(c1);
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Citation c1 = new Citation("chunk-1", "doc-1", "text", 0.95);
        Citation c2 = new Citation("chunk-1", "doc-1", "text", 0.95);

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Citation citation = new Citation("chunk-1", "doc-1", "text", 0.95);

        assertThat(citation.toString())
            .contains("Citation")
            .contains("chunk-1")
            .contains("doc-1")
            .contains("0.95");
    }
}
