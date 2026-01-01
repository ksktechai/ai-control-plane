package com.ai.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RetrievalStrategyTest {

    @Test
    void shouldHaveCorrectDefaultTopKForSimple() {
        assertThat(RetrievalStrategy.SIMPLE.defaultTopK()).isEqualTo(5);
    }

    @Test
    void shouldHaveCorrectDefaultTopKForDeep() {
        assertThat(RetrievalStrategy.DEEP.defaultTopK()).isEqualTo(10);
    }

    @Test
    void shouldHaveCorrectDefaultTopKForExhaustive() {
        assertThat(RetrievalStrategy.EXHAUSTIVE.defaultTopK()).isEqualTo(20);
    }

    @Test
    void shouldHaveAllEnumValues() {
        assertThat(RetrievalStrategy.values()).hasSize(3);
        assertThat(RetrievalStrategy.values())
                .containsExactly(
                        RetrievalStrategy.SIMPLE,
                        RetrievalStrategy.DEEP,
                        RetrievalStrategy.EXHAUSTIVE);
    }

    @Test
    void shouldHaveCorrectEnumNames() {
        assertThat(RetrievalStrategy.SIMPLE.name()).isEqualTo("SIMPLE");
        assertThat(RetrievalStrategy.DEEP.name()).isEqualTo("DEEP");
        assertThat(RetrievalStrategy.EXHAUSTIVE.name()).isEqualTo("EXHAUSTIVE");
    }
}
