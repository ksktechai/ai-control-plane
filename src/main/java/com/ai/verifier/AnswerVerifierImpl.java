package com.ai.verifier;

import com.ai.domain.Answer;
import com.ai.domain.Chunk;
import com.ai.domain.Claim;
import com.ai.domain.RetrievalResult;
import com.ai.domain.VerificationResult;
import com.ai.domain.VerificationStatus;
import com.ai.llm.OllamaClient;
import com.ai.model.LlmModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/** Implementation of answer verifier using LLM for claim extraction and verification. */
@Service
public class AnswerVerifierImpl implements AnswerVerifier {
    private static final Logger logger = LogManager.getLogger(AnswerVerifierImpl.class);
    private static final LlmModel VERIFICATION_MODEL = LlmModel.PHI_3_MINI;
    private static final int MAX_VERIFICATION_TOKENS = 500;

    private final OllamaClient ollamaClient;

    public AnswerVerifierImpl(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    @Override
    public VerificationResult verify(Answer answer, RetrievalResult context) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        logger.info(
                "Starting answer verification - answerLength: {}, contextChunks: {}",
                answer.text().length(),
                context.chunks().size());

        // Extract claims from answer
        List<Claim> claims = extractClaims(answer.text());
        logger.debug("Extracted {} claims from answer", claims.size());

        // Verify each claim against context
        List<Claim> verifiedClaims = new ArrayList<>();
        int groundedCount = 0;

        for (Claim claim : claims) {
            boolean isGrounded = verifyClaimAgainstContext(claim.text(), context.chunks());
            String supportingChunkId =
                    isGrounded && !context.chunks().isEmpty() ? context.chunks().get(0).id() : null;

            Claim verifiedClaim = new Claim(claim.text(), isGrounded, supportingChunkId);
            verifiedClaims.add(verifiedClaim);

            if (isGrounded) {
                groundedCount++;
            }
        }

        // Calculate grounding score
        double groundingScore = claims.isEmpty() ? 1.0 : (double) groundedCount / claims.size();

        // Determine verification status
        VerificationStatus status;
        if (groundingScore >= 0.9) {
            status = VerificationStatus.GROUNDED;
        } else if (groundingScore >= 0.5) {
            status = VerificationStatus.PARTIALLY_GROUNDED;
        } else {
            status = VerificationStatus.UNGROUNDED;
        }

        String reasoning =
                String.format(
                        "Verified %d/%d claims as grounded (%.2f%% grounding)",
                        groundedCount, claims.size(), groundingScore * 100);

        logger.info(
                "Verification completed - status: {}, groundingScore: {:.2f}",
                status,
                groundingScore);

        return new VerificationResult(status, verifiedClaims, groundingScore, reasoning);
    }

    private List<Claim> extractClaims(String answerText) {
        String prompt =
                String.format(
                        "Extract the main factual claims from this text. "
                                + "List each claim on a separate line, numbered.\n\n"
                                + "Text: %s\n\n"
                                + "Claims:",
                        answerText);

        try {
            String response =
                    ollamaClient.generate(VERIFICATION_MODEL, prompt, MAX_VERIFICATION_TOKENS);
            return parseClaimsFromResponse(response);
        } catch (Exception e) {
            logger.warn(
                    "Failed to extract claims via LLM, treating answer as single claim: {}",
                    e.getMessage());
            return List.of(new Claim(answerText, false, null));
        }
    }

    private List<Claim> parseClaimsFromResponse(String response) {
        List<Claim> claims = new ArrayList<>();
        String[] lines = response.split("\n");

        for (String line : lines) {
            String cleaned =
                    line.trim()
                            .replaceAll("^\\d+\\.\\s*", "") // Remove "1. " prefix
                            .replaceAll("^-\\s*", ""); // Remove "- " prefix

            if (!cleaned.isBlank() && cleaned.length() > 10) {
                claims.add(new Claim(cleaned, false, null));
            }
        }

        return claims.isEmpty() ? List.of(new Claim(response.trim(), false, null)) : claims;
    }

    private boolean verifyClaimAgainstContext(String claim, List<Chunk> chunks) {
        if (chunks.isEmpty()) {
            return false;
        }

        String contextText =
                chunks.stream()
                        .map(Chunk::text)
                        .limit(3) // Use top 3 chunks
                        .reduce((a, b) -> a + "\n\n" + b)
                        .orElse("");

        String prompt =
                String.format(
                        "Does the following context support the claim? Answer only 'yes' or 'no'.\n\n"
                                + "Context: %s\n\n"
                                + "Claim: %s\n\n"
                                + "Answer:",
                        contextText, claim);

        try {
            String response = ollamaClient.generate(VERIFICATION_MODEL, prompt, 10);
            boolean isGrounded = response.toLowerCase().trim().startsWith("yes");
            logger.debug("Claim verification - claim: '{}', grounded: {}", claim, isGrounded);
            return isGrounded;
        } catch (Exception e) {
            logger.warn("Failed to verify claim, assuming not grounded: {}", e.getMessage());
            return false;
        }
    }
}
