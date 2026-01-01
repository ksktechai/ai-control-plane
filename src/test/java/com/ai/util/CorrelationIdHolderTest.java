package com.ai.util;

import com.ai.util.CorrelationIdHolder;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CorrelationIdHolderTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        CorrelationIdHolder.clearAll();
    }

    @Test
    void shouldSetCorrelationId() {
        String correlationId = "test-correlation-id-123";

        CorrelationIdHolder.set(correlationId);

        assertThat(CorrelationIdHolder.get()).isEqualTo(correlationId);
    }

    @Test
    void shouldThrowExceptionForNullCorrelationId() {
        assertThatThrownBy(() -> CorrelationIdHolder.set(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Correlation ID cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForBlankCorrelationId() {
        assertThatThrownBy(() -> CorrelationIdHolder.set("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Correlation ID cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForEmptyCorrelationId() {
        assertThatThrownBy(() -> CorrelationIdHolder.set(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Correlation ID cannot be null or blank");
    }

    @Test
    void shouldReturnNullWhenNotSet() {
        assertThat(CorrelationIdHolder.get()).isNull();
    }

    @Test
    void shouldGenerateCorrelationId() {
        String generated = CorrelationIdHolder.generate();

        assertThat(generated).isNotNull().isNotBlank();
        assertThat(CorrelationIdHolder.get()).isEqualTo(generated);
    }

    @Test
    void shouldGenerateUniqueCorrelationIds() {
        CorrelationIdHolder.clearAll();
        String first = CorrelationIdHolder.generate();
        CorrelationIdHolder.clearAll();
        String second = CorrelationIdHolder.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldGetOrGenerateWhenNotSet() {
        String result = CorrelationIdHolder.getOrGenerate();

        assertThat(result).isNotNull().isNotBlank();
        assertThat(CorrelationIdHolder.get()).isEqualTo(result);
    }

    @Test
    void shouldGetOrGenerateWhenAlreadySet() {
        String existing = "existing-correlation-id";
        CorrelationIdHolder.set(existing);

        String result = CorrelationIdHolder.getOrGenerate();

        assertThat(result).isEqualTo(existing);
    }

    @Test
    void shouldClearCorrelationId() {
        CorrelationIdHolder.set("test-id");

        CorrelationIdHolder.clear();

        assertThat(CorrelationIdHolder.get()).isNull();
    }

    @Test
    void shouldClearAllMdcData() {
        CorrelationIdHolder.set("test-id");
        ThreadContext.put("otherKey", "otherValue");

        CorrelationIdHolder.clearAll();

        assertThat(CorrelationIdHolder.get()).isNull();
        assertThat(ThreadContext.get("otherKey")).isNull();
    }
}
