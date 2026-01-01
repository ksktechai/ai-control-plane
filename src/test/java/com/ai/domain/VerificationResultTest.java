package com.ai.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class VerificationResultTest {

    @Test
    void shouldCreateValidVerificationResult() {
        List<Claim> claims = List.of(new Claim("AI is a technology", true, "chunk-1"));
        VerificationResult result =
                new VerificationResult(
                        VerificationStatus.GROUNDED, claims, 0.95, "All claims verified");

        assertThat(result.status()).isEqualTo(VerificationStatus.GROUNDED);
        assertThat(result.claims()).hasSize(1);
        assertThat(result.groundingScore()).isEqualTo(0.95);
        assertThat(result.reasoning()).isEqualTo("All claims verified");
    }

    @Test
    void shouldCreateResultWithEmptyClaims() {
        VerificationResult result =
                new VerificationResult(
                        VerificationStatus.GROUNDED, List.of(), 1.0, "No claims to verify");

        assertThat(result.claims()).isEmpty();
    }

    @Test
    void shouldRejectNullStatus() {
        assertThatThrownBy(() -> new VerificationResult(null, List.of(), 0.95, "reasoning"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Status cannot be null");
    }

    @Test
    void shouldRejectNullClaims() {
        assertThatThrownBy(
                        () ->
                                new VerificationResult(
                                        VerificationStatus.GROUNDED, null, 0.95, "reasoning"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Claims cannot be null");
    }

    @Test
    void shouldRejectNegativeGroundingScore() {
        assertThatThrownBy(
                        () ->
                                new VerificationResult(
                                        VerificationStatus.GROUNDED, List.of(), -0.1, "reasoning"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Grounding score must be between 0.0 and 1.0");
    }

    @Test
    void shouldRejectGroundingScoreAboveOne() {
        assertThatThrownBy(
                        () ->
                                new VerificationResult(
                                        VerificationStatus.GROUNDED, List.of(), 1.1, "reasoning"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Grounding score must be between 0.0 and 1.0");
    }

    @Test
    void shouldRejectNullReasoning() {
        assertThatThrownBy(
                        () ->
                                new VerificationResult(
                                        VerificationStatus.GROUNDED, List.of(), 0.95, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reasoning cannot be null or blank");
    }

    @Test
    void shouldRejectBlankReasoning() {
        assertThatThrownBy(
                        () ->
                                new VerificationResult(
                                        VerificationStatus.GROUNDED, List.of(), 0.95, "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reasoning cannot be null or blank");
    }

    @Test
    void shouldReturnImmutableClaims() {
        List<Claim> claims = new ArrayList<>();
        claims.add(new Claim("AI is a technology", true, "chunk-1"));
        VerificationResult result =
                new VerificationResult(VerificationStatus.GROUNDED, claims, 0.95, "reasoning");

        assertThatThrownBy(() -> result.claims().add(new Claim("Another claim", true, "chunk-2")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        List<Claim> claims = List.of(new Claim("AI is a technology", true, "chunk-1"));
        List<Claim> differentClaims = List.of(new Claim("Different claim", false, null));
        VerificationResult v1 =
                new VerificationResult(VerificationStatus.GROUNDED, claims, 0.95, "reasoning");
        VerificationResult v2 =
                new VerificationResult(VerificationStatus.GROUNDED, claims, 0.95, "reasoning");
        VerificationResult v3 =
                new VerificationResult(VerificationStatus.UNGROUNDED, claims, 0.95, "reasoning");
        VerificationResult v4 =
                new VerificationResult(
                        VerificationStatus.GROUNDED, differentClaims, 0.95, "reasoning");
        VerificationResult v5 =
                new VerificationResult(VerificationStatus.GROUNDED, claims, 0.85, "reasoning");
        VerificationResult v6 =
                new VerificationResult(
                        VerificationStatus.GROUNDED, claims, 0.95, "different reasoning");

        assertThat(v1).isEqualTo(v2);
        assertThat(v1).isNotEqualTo(v3);
        assertThat(v1).isNotEqualTo(v4);
        assertThat(v1).isNotEqualTo(v5);
        assertThat(v1).isNotEqualTo(v6);
        assertThat(v1).isEqualTo(v1);
        assertThat(v1).isNotEqualTo(null);
        assertThat(v1).isNotEqualTo("not a VerificationResult");
    }

    @Test
    void shouldImplementHashCodeCorrectly() {
        List<Claim> claims = List.of(new Claim("AI is a technology", true, "chunk-1"));
        VerificationResult v1 =
                new VerificationResult(VerificationStatus.GROUNDED, claims, 0.95, "reasoning");
        VerificationResult v2 =
                new VerificationResult(VerificationStatus.GROUNDED, claims, 0.95, "reasoning");

        assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
    }

    @Test
    void shouldImplementToStringCorrectly() {
        List<Claim> claims = List.of(new Claim("AI is a technology", true, "chunk-1"));
        VerificationResult result =
                new VerificationResult(VerificationStatus.GROUNDED, claims, 0.95, "reasoning");

        assertThat(result.toString())
                .contains("VerificationResult")
                .contains("GROUNDED")
                .contains("0.95")
                .contains("claimCount=1");
    }
}
