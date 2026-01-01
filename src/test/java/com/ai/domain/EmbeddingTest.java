package com.ai.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmbeddingTest {

    @Test
    void shouldCreateValidEmbedding() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        Embedding embedding = new Embedding(vector, "nomic-embed-text");

        assertThat(embedding.vector()).containsExactly(0.1f, 0.2f, 0.3f);
        assertThat(embedding.model()).isEqualTo("nomic-embed-text");
        assertThat(embedding.dimension()).isEqualTo(3);
    }

    @Test
    void shouldCopyVectorOnConstruction() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        Embedding embedding = new Embedding(vector, "nomic-embed-text");

        vector[0] = 999f;

        assertThat(embedding.vector()[0]).isEqualTo(0.1f);
    }

    @Test
    void shouldReturnCopyOfVector() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        Embedding embedding = new Embedding(vector, "nomic-embed-text");

        float[] retrieved = embedding.vector();
        retrieved[0] = 999f;

        assertThat(embedding.vector()[0]).isEqualTo(0.1f);
    }

    @Test
    void shouldRejectNullVector() {
        assertThatThrownBy(() -> new Embedding(null, "nomic-embed-text"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Vector cannot be null or empty");
    }

    @Test
    void shouldRejectEmptyVector() {
        assertThatThrownBy(() -> new Embedding(new float[]{}, "nomic-embed-text"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Vector cannot be null or empty");
    }

    @Test
    void shouldRejectNullModel() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        assertThatThrownBy(() -> new Embedding(vector, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model cannot be null or blank");
    }

    @Test
    void shouldRejectBlankModel() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        assertThatThrownBy(() -> new Embedding(vector, "  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Model cannot be null or blank");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        Embedding e1 = new Embedding(vector, "nomic-embed-text");
        Embedding e2 = new Embedding(vector, "nomic-embed-text");
        Embedding e3 = new Embedding(new float[]{0.4f, 0.5f, 0.6f}, "nomic-embed-text");
        Embedding e4 = new Embedding(vector, "different-model");

        assertThat(e1).isEqualTo(e2);
        assertThat(e1).isNotEqualTo(e3);
        assertThat(e1).isNotEqualTo(e4);
        assertThat(e1).isEqualTo(e1);
        assertThat(e1).isNotEqualTo(null);
        assertThat(e1).isNotEqualTo("not an Embedding");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        Embedding e1 = new Embedding(vector, "nomic-embed-text");
        Embedding e2 = new Embedding(vector, "nomic-embed-text");

        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        float[] vector = {0.1f, 0.2f, 0.3f};
        Embedding embedding = new Embedding(vector, "nomic-embed-text");

        assertThat(embedding.toString())
            .contains("Embedding")
            .contains("nomic-embed-text")
            .contains("dimension=3");
    }
}
