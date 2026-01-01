package com.ai.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClaimTest {

    @Test
    void shouldCreateGroundedClaim() {
        Claim claim = new Claim("AI is a technology", true, "chunk-1");

        assertThat(claim.text()).isEqualTo("AI is a technology");
        assertThat(claim.isGrounded()).isTrue();
        assertThat(claim.supportingChunkId()).isEqualTo("chunk-1");
    }

    @Test
    void shouldCreateUngroundedClaim() {
        Claim claim = new Claim("AI is magic", false, null);

        assertThat(claim.text()).isEqualTo("AI is magic");
        assertThat(claim.isGrounded()).isFalse();
        assertThat(claim.supportingChunkId()).isNull();
    }

    @Test
    void shouldRejectNullText() {
        assertThatThrownBy(() -> new Claim(null, true, "chunk-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Claim text cannot be null or blank");
    }

    @Test
    void shouldRejectBlankText() {
        assertThatThrownBy(() -> new Claim("  ", true, "chunk-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Claim text cannot be null or blank");
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        Claim c1 = new Claim("AI is a technology", true, "chunk-1");
        Claim c2 = new Claim("AI is a technology", true, "chunk-1");
        Claim c3 = new Claim("Different claim", true, "chunk-1");
        Claim c4 = new Claim("AI is a technology", false, "chunk-1");
        Claim c5 = new Claim("AI is a technology", true, "chunk-2");
        Claim c6 = new Claim("AI is a technology", true, null);

        assertThat(c1).isEqualTo(c2);
        assertThat(c1).isNotEqualTo(c3);
        assertThat(c1).isNotEqualTo(c4);
        assertThat(c1).isNotEqualTo(c5);
        assertThat(c1).isNotEqualTo(c6);
        assertThat(c1).isEqualTo(c1);
        assertThat(c1).isNotEqualTo(null);
        assertThat(c1).isNotEqualTo("not a Claim");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        Claim c1 = new Claim("AI is a technology", true, "chunk-1");
        Claim c2 = new Claim("AI is a technology", true, "chunk-1");

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        Claim claim = new Claim("AI is a technology", true, "chunk-1");

        assertThat(claim.toString())
                .contains("Claim")
                .contains("AI is a technology")
                .contains("isGrounded=true")
                .contains("chunk-1");
    }
}
