package com.ai.rag;

import com.ai.domain.Chunk;
import com.ai.domain.Embedding;
import com.ai.domain.RetrievalResult;
import com.ai.embeddings.EmbeddingService;
import com.ai.model.RetrievalStrategy;
import com.ai.util.CorrelationIdHolder;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/** Implementation of retrieval service using vector similarity search. */
@Service
public class RetrievalServiceImpl implements RetrievalService {
    private static final Logger logger = LogManager.getLogger(RetrievalServiceImpl.class);

    private final EmbeddingService embeddingService;
    private final ChunkRepository chunkRepository;

    public RetrievalServiceImpl(
            EmbeddingService embeddingService, ChunkRepository chunkRepository) {
        this.embeddingService = embeddingService;
        this.chunkRepository = chunkRepository;
    }

    @Override
    public RetrievalResult retrieve(String question, RetrievalStrategy strategy) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or blank");
        }
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }

        String correlationId = CorrelationIdHolder.getOrGenerate();
        long startTime = System.currentTimeMillis();

        logger.info(
                "Starting retrieval - correlationId: {}, strategy: {}, topK: {}",
                correlationId,
                strategy,
                strategy.defaultTopK());

        // Generate embedding for question
        Embedding queryEmbedding = embeddingService.generateEmbedding(question);
        logger.debug("Generated query embedding - dimension: {}", queryEmbedding.dimension());

        // Retrieve similar chunks
        List<Chunk> chunks =
                chunkRepository.findSimilar(queryEmbedding.vector(), strategy.defaultTopK());

        long duration = System.currentTimeMillis() - startTime;

        logger.info(
                "Retrieval completed - correlationId: {}, chunksFound: {}, durationMs: {}",
                correlationId,
                chunks.size(),
                duration);

        chunks.forEach(
                chunk ->
                        logger.debug(
                                "Retrieved chunk: {} from document: {}",
                                chunk.id(),
                                chunk.documentId()));

        return new RetrievalResult(chunks, strategy.name(), duration);
    }
}
