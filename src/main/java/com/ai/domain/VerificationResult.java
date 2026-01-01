package com.ai.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * Represents the result of answer verification.
 */
public final class VerificationResult {
    private final VerificationStatus status;
    private final List<Claim> claims;
    private final double groundingScore;
    private final String reasoning;

    @JsonCreator
    public VerificationResult(
            @JsonProperty("status") VerificationStatus status,
            @JsonProperty("claims") List<Claim> claims,
            @JsonProperty("groundingScore") double groundingScore,
            @JsonProperty("reasoning") String reasoning) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (claims == null) {
            throw new IllegalArgumentException("Claims cannot be null");
        }
        if (groundingScore < 0.0 || groundingScore > 1.0) {
            throw new IllegalArgumentException("Grounding score must be between 0.0 and 1.0");
        }
        if (reasoning == null || reasoning.isBlank()) {
            throw new IllegalArgumentException("Reasoning cannot be null or blank");
        }
        this.status = status;
        this.claims = List.copyOf(claims);
        this.groundingScore = groundingScore;
        this.reasoning = reasoning;
    }

    @JsonGetter("status")
    public VerificationStatus status() {
        return status;
    }

    @JsonGetter("claims")
    public List<Claim> claims() {
        return claims;
    }

    @JsonGetter("groundingScore")
    public double groundingScore() {
        return groundingScore;
    }

    @JsonGetter("reasoning")
    public String reasoning() {
        return reasoning;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VerificationResult other)) return false;
        return status == other.status &&
               Objects.equals(claims, other.claims) &&
               Double.compare(groundingScore, other.groundingScore) == 0 &&
               Objects.equals(reasoning, other.reasoning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, claims, groundingScore, reasoning);
    }

    @Override
    public String toString() {
        return "VerificationResult[status=" + status +
               ", groundingScore=" + groundingScore +
               ", claimCount=" + claims.size() + "]";
    }
}
