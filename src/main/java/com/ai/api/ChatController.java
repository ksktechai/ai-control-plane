package com.ai.api;

import com.ai.common.domain.AnswerResult;
import com.ai.common.domain.Question;
import com.ai.common.dto.ChatRequest;
import com.ai.common.dto.ChatResponse;
import com.ai.common.util.CorrelationIdHolder;
import com.ai.control.ControlPlane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for chat endpoint.
 */
@RestController
@RequestMapping("/api")
public class ChatController {
    private static final Logger logger = LogManager.getLogger(ChatController.class);

    private final ControlPlane controlPlane;

    public ChatController(ControlPlane controlPlane) {
        this.controlPlane = controlPlane;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String correlationId = CorrelationIdHolder.getOrGenerate();

        logger.info("Received chat request - correlationId: {}, questionLength: {}",
            correlationId, request.question().length());

        try {
            Question question = new Question(request.question(), correlationId);
            AnswerResult result = controlPlane.answer(question);

            ChatResponse response = new ChatResponse(
                result.answer().text(),
                result.answer().citations(),
                result.confidence(),
                result.answer().modelUsed(),
                result.retrievalStrategy(),
                result.verification().status().name()
            );

            logger.info("Chat request completed - correlationId: {}, confidence: {:.2f}",
                correlationId, result.confidence());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Chat request failed - correlationId: {}", correlationId, e);
            throw e;
        } finally {
            CorrelationIdHolder.clear();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP"));
    }

    private record HealthResponse(String status) {
    }
}
