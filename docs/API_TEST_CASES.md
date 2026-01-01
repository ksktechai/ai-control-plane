# API Test Cases

This document provides comprehensive test cases for the AI Control Plane API.

## Quick Start

```bash
# Run all test cases
./test-api-requests.sh

# Or run individual tests with curl
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is AI?"}'
```

## Test Categories

### 1. Simple Factual Questions
**Expected Behavior:** Should use PHI_3_MINI model with SIMPLE retrieval strategy. High confidence, grounded answer.

```bash
# Test 1.1: Basic definition
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is AI?"}'

# Test 1.2: Component question
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is machine learning?"}'

# Test 1.3: Yes/No question
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Is AI related to machine learning?"}'
```

**Expected Response:**
- `modelUsed`: `phi3:mini`
- `retrievalStrategy`: `SIMPLE`
- `verificationStatus`: `GROUNDED`
- `confidence`: > 0.7
- `citations`: 1-2 chunks

---

### 2. Complex Multi-Part Questions
**Expected Behavior:** May trigger escalation to QWEN_2_5_7B with DEEP retrieval if initial confidence is low.

```bash
# Test 2.1: Multi-concept question
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is AI and how does it relate to machine learning, natural language processing, and computer vision?"}'

# Test 2.2: Comparison question
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Compare and contrast AI with machine learning"}'

# Test 2.3: Analytical question
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "How does machine learning enable AI systems to learn from data?"}'
```

**Expected Response:**
- First attempt: `phi3:mini` + `SIMPLE`
- If low confidence: Escalate to `qwen2.5:7b` + `DEEP`
- `confidence`: > 0.7 (after retry if needed)
- `citations`: 2-5 chunks

---

### 3. Edge Cases

#### 3.1: Empty or Malformed Requests
```bash
# Empty question
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": ""}'

# Missing question field
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{}'

# Invalid JSON
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d 'invalid json'
```

**Expected Response:** 400 Bad Request with error message

#### 3.2: Very Long Questions
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "Can you provide a comprehensive, detailed, and exhaustive explanation of artificial intelligence, including its history, current applications, future potential, ethical considerations, technical implementations, and societal impact?"}'
```

**Expected Response:** Should handle gracefully, may use larger model

#### 3.3: Questions Outside Knowledge Base
```bash
# Question about content not in the database
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is quantum computing?"}'
```

**Expected Response:**
- `verificationStatus`: `UNGROUNDED` or `PARTIAL_GROUNDING`
- `confidence`: Low (< 0.7)
- May still provide an answer but with low confidence

#### 3.4: Ambiguous Questions
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is it?"}'
```

**Expected Response:** Best effort answer with low confidence

---

### 4. Correlation ID Testing

```bash
# Custom correlation ID
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: custom-trace-123" \
  -d '{"question": "What is AI?"}'

# Auto-generated correlation ID (omit header)
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What is AI?"}'
```

**Expected Response:** Correlation ID appears in logs and can be used for tracing

---

### 5. Health Check

```bash
# Health endpoint
curl -X GET http://localhost:8080/api/health

# Expected response
{
  "status": "UP"
}
```

---

## Response Schema

All successful `/api/chat` requests return:

```json
{
  "answer": "string",
  "citations": [
    {
      "chunkId": "string",
      "documentId": "string",
      "text": "string",
      "relevanceScore": 0.95
    }
  ],
  "confidence": 0.85,
  "modelUsed": "phi3:mini",
  "retrievalStrategy": "SIMPLE",
  "verificationStatus": "GROUNDED"
}
```

### Field Descriptions

- **answer**: The generated answer text
- **citations**: Array of source chunks used to generate the answer
  - `chunkId`: Unique identifier for the chunk
  - `documentId`: Source document identifier
  - `text`: The actual text content of the chunk
  - `relevanceScore`: Vector similarity score (0-1)
- **confidence**: Overall confidence score (0-1)
  - `< 0.7`: Low confidence, may trigger retry
  - `0.7-0.85`: Medium confidence
  - `> 0.85`: High confidence
- **modelUsed**: LLM model used for generation
  - `phi3:mini`: Small model (first attempt)
  - `qwen2.5:7b`: Medium model (second attempt)
  - `qwen2.5:14b`: Large model (third attempt, rarely used)
- **retrievalStrategy**: RAG retrieval strategy used
  - `SIMPLE`: Top 5 chunks
  - `DEEP`: Top 10 chunks
  - `EXHAUSTIVE`: Top 20 chunks
- **verificationStatus**: Grounding verification result
  - `GROUNDED`: All claims verified against context
  - `PARTIAL_GROUNDING`: Some claims verified
  - `UNGROUNDED`: Claims not supported by context

---

## Retry Logic Flow

1. **Attempt 1:**
   - Model: `phi3:mini` (3.8B)
   - Strategy: `SIMPLE` (top 5)
   - Max tokens: 256

2. **Attempt 2 (if confidence < 0.7):**
   - Model: `qwen2.5:7b` (7B)
   - Strategy: `DEEP` (top 10)
   - Max tokens: 512

3. **Attempt 3 (if confidence still < 0.7):**
   - Model: `qwen2.5:14b` (14B)
   - Strategy: `EXHAUSTIVE` (top 20)
   - Max tokens: 1024

---

## Performance Expectations

| Question Type | Expected Time | Model Used |
|---------------|--------------|------------|
| Simple factual | 2-5 seconds | phi3:mini |
| Complex analysis | 5-10 seconds | qwen2.5:7b |
| Very complex | 10-20 seconds | qwen2.5:14b |

*Times may vary based on hardware*

---

## Testing Tips

1. **Check logs** for detailed execution flow:
   ```bash
   tail -f logs/application.log
   ```

2. **Monitor correlation IDs** to trace requests through the system

3. **Test incrementally** - start with simple questions, then increase complexity

4. **Verify citations** - ensure answers reference actual chunks from the database

5. **Test retry logic** - ask questions outside the knowledge base to trigger escalation

6. **Load testing** - use parallel requests to test concurrency:
   ```bash
   # Run 10 concurrent requests
   for i in {1..10}; do
     curl -X POST http://localhost:8080/api/chat \
       -H "Content-Type: application/json" \
       -d '{"question": "What is AI?"}' &
   done
   wait
   ```

---

## Common Issues

### Issue: 404 Model Not Found
**Solution:** Ensure required models are pulled:
```bash
ollama list | grep -E "(phi3:mini|qwen2.5:7b|nomic-embed-text)"
```

### Issue: Empty Citations
**Solution:** Check that sample data is loaded:
```bash
docker exec ai-postgres psql -U aiuser -d aidb -c "SELECT COUNT(*) FROM chunks;"
```

### Issue: Low Confidence on Simple Questions
**Solution:** May indicate embedding quality issues or insufficient context in database

### Issue: Timeout Errors
**Solution:** Increase model timeout or use smaller models for testing
