package com.ai.rag;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.ai.domain.Chunk;
import com.ai.domain.Embedding;
import com.ai.domain.RetrievalResult;
import com.ai.embeddings.EmbeddingService;
import com.ai.model.RetrievalStrategy;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetrievalServiceImplTest {

    @Mock private EmbeddingService embeddingService;

    @Mock private ChunkRepository chunkRepository;

    private RetrievalService retrievalService;

    @BeforeEach
    void setUp() {
        retrievalService = new RetrievalServiceImpl(embeddingService, chunkRepository);
    }

    @Test
    void shouldRetrieveRelevantChunks() {
        Embedding queryEmbedding =
                new Embedding(new float[] {0.1f, 0.2f, 0.3f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "sample text", 0, queryEmbedding);

        when(embeddingService.generateEmbedding("What is AI?")).thenReturn(queryEmbedding);
        when(chunkRepository.findSimilar(any(float[].class), anyInt())).thenReturn(List.of(chunk));

        RetrievalResult result = retrievalService.retrieve("What is AI?", RetrievalStrategy.SIMPLE);

        assertThat(result).isNotNull();
        assertThat(result.chunks()).hasSize(1);
        assertThat(result.strategy()).isEqualTo("SIMPLE");
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldThrowExceptionForNullQuestion() {
        assertThatThrownBy(() -> retrievalService.retrieve(null, RetrievalStrategy.SIMPLE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionForNullStrategy() {
        assertThatThrownBy(() -> retrievalService.retrieve("What is AI?", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Strategy cannot be null");
    }

    @Test
    void shouldThrowExceptionForBlankQuestion() {
        assertThatThrownBy(() -> retrievalService.retrieve("  ", RetrievalStrategy.SIMPLE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question cannot be null or blank");
    }

    @Test
    void shouldRetrieveWithDeepStrategy() {
        Embedding queryEmbedding =
                new Embedding(new float[] {0.1f, 0.2f, 0.3f}, "nomic-embed-text");
        Chunk chunk1 = new Chunk("chunk-1", "doc-1", "text1", 0, queryEmbedding);
        Chunk chunk2 = new Chunk("chunk-2", "doc-2", "text2", 1, queryEmbedding);

        when(embeddingService.generateEmbedding("What is AI?")).thenReturn(queryEmbedding);
        when(chunkRepository.findSimilar(any(float[].class), anyInt()))
                .thenReturn(List.of(chunk1, chunk2));

        RetrievalResult result = retrievalService.retrieve("What is AI?", RetrievalStrategy.DEEP);

        assertThat(result).isNotNull();
        assertThat(result.chunks()).hasSize(2);
        assertThat(result.strategy()).isEqualTo("DEEP");
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldRetrieveWithExhaustiveStrategy() {
        Embedding queryEmbedding =
                new Embedding(new float[] {0.1f, 0.2f, 0.3f}, "nomic-embed-text");
        Chunk chunk = new Chunk("chunk-1", "doc-1", "sample text", 0, queryEmbedding);

        when(embeddingService.generateEmbedding("What is AI?")).thenReturn(queryEmbedding);
        when(chunkRepository.findSimilar(any(float[].class), anyInt())).thenReturn(List.of(chunk));

        RetrievalResult result =
                retrievalService.retrieve("What is AI?", RetrievalStrategy.EXHAUSTIVE);

        assertThat(result).isNotNull();
        assertThat(result.chunks()).hasSize(1);
        assertThat(result.strategy()).isEqualTo("EXHAUSTIVE");
    }

    @Test
    void shouldHandleEmptyResults() {
        Embedding queryEmbedding =
                new Embedding(new float[] {0.1f, 0.2f, 0.3f}, "nomic-embed-text");

        when(embeddingService.generateEmbedding("What is AI?")).thenReturn(queryEmbedding);
        when(chunkRepository.findSimilar(any(float[].class), anyInt())).thenReturn(List.of());

        RetrievalResult result = retrievalService.retrieve("What is AI?", RetrievalStrategy.SIMPLE);

        assertThat(result).isNotNull();
        assertThat(result.chunks()).isEmpty();
        assertThat(result.strategy()).isEqualTo("SIMPLE");
    }
}
