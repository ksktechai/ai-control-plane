package com.ai.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a vector embedding.
 */
public final class Embedding {
    private final float[] vector;
    private final String model;

    @JsonCreator
    public Embedding(
            @JsonProperty("vector") float[] vector,
            @JsonProperty("model") String model) {
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("Vector cannot be null or empty");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model cannot be null or blank");
        }
        this.vector = Arrays.copyOf(vector, vector.length);
        this.model = model;
    }

    @JsonGetter("vector")
    public float[] vector() {
        return Arrays.copyOf(vector, vector.length);
    }

    @JsonGetter("model")
    public String model() {
        return model;
    }

    @JsonGetter("dimension")
    public int dimension() {
        return vector.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Embedding other)) return false;
        return Arrays.equals(vector, other.vector) &&
               Objects.equals(model, other.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(vector), model);
    }

    @Override
    public String toString() {
        return "Embedding[model=" + model + ", dimension=" + vector.length + "]";
    }
}
