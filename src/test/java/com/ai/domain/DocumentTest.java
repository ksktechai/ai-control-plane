package com.ai.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class DocumentTest {

    private Instant createTestTime() {
        return Instant.parse("2025-01-01T00:00:00Z");
    }

    @Test
    void shouldCreateValidDocument() {
        Instant now = createTestTime();
        Document doc = new Document("doc-1", "AI Basics", "Content", "source.pdf", now);

        assertThat(doc.id()).isEqualTo("doc-1");
        assertThat(doc.title()).isEqualTo("AI Basics");
        assertThat(doc.content()).isEqualTo("Content");
        assertThat(doc.source()).isEqualTo("source.pdf");
        assertThat(doc.createdAt()).isEqualTo(now);
    }

    @Test
    void shouldRejectNullId() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document(null, "title", "content", "source", now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document ID cannot be null or blank");
    }

    @Test
    void shouldRejectBlankId() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document("  ", "title", "content", "source", now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document ID cannot be null or blank");
    }

    @Test
    void shouldRejectNullTitle() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document("doc-1", null, "content", "source", now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document title cannot be null or blank");
    }

    @Test
    void shouldRejectBlankTitle() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document("doc-1", "  ", "content", "source", now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document title cannot be null or blank");
    }

    @Test
    void shouldRejectNullContent() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document("doc-1", "title", null, "source", now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document content cannot be null or blank");
    }

    @Test
    void shouldRejectBlankContent() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document("doc-1", "title", "  ", "source", now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document content cannot be null or blank");
    }

    @Test
    void shouldRejectNullSource() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document("doc-1", "title", "content", null, now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document source cannot be null or blank");
    }

    @Test
    void shouldRejectBlankSource() {
        Instant now = createTestTime();
        assertThatThrownBy(() -> new Document("doc-1", "title", "content", "  ", now))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Document source cannot be null or blank");
    }

    @Test
    void shouldRejectNullCreatedAt() {
        assertThatThrownBy(() -> new Document("doc-1", "title", "content", "source", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Created timestamp cannot be null");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Instant now = createTestTime();
        Instant later = now.plusSeconds(60);
        Document d1 = new Document("doc-1", "AI Basics", "Content", "source.pdf", now);
        Document d2 = new Document("doc-1", "AI Basics", "Content", "source.pdf", now);
        Document d3 = new Document("doc-2", "AI Basics", "Content", "source.pdf", now);
        Document d4 = new Document("doc-1", "Different Title", "Content", "source.pdf", now);
        Document d5 = new Document("doc-1", "AI Basics", "Different Content", "source.pdf", now);
        Document d6 = new Document("doc-1", "AI Basics", "Content", "different.pdf", now);
        Document d7 = new Document("doc-1", "AI Basics", "Content", "source.pdf", later);

        assertThat(d1).isEqualTo(d2);
        assertThat(d1).isNotEqualTo(d3);
        assertThat(d1).isNotEqualTo(d4);
        assertThat(d1).isNotEqualTo(d5);
        assertThat(d1).isNotEqualTo(d6);
        assertThat(d1).isNotEqualTo(d7);
        assertThat(d1).isEqualTo(d1);
        assertThat(d1).isNotEqualTo(null);
        assertThat(d1).isNotEqualTo("not a Document");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Instant now = createTestTime();
        Document d1 = new Document("doc-1", "AI Basics", "Content", "source.pdf", now);
        Document d2 = new Document("doc-1", "AI Basics", "Content", "source.pdf", now);

        assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Instant now = createTestTime();
        Document doc = new Document("doc-1", "AI Basics", "Content", "source.pdf", now);

        assertThat(doc.toString())
            .contains("Document")
            .contains("doc-1")
            .contains("AI Basics")
            .contains("source.pdf");
    }
}
