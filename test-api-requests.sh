#!/bin/bash

# AI Control Plane - API Test Suite
# This script contains various test cases to demonstrate the RAG system capabilities

BASE_URL="http://localhost:8080"
CORRELATION_ID="test-$(date +%s)"

echo "==================================="
echo "AI Control Plane API Test Suite"
echo "==================================="
echo ""

# Helper function to make requests
test_request() {
    local test_name=$1
    local question=$2
    local correlation_id="${3:-$CORRELATION_ID}"

    echo "-----------------------------------"
    echo "TEST: $test_name"
    echo "QUESTION: $question"
    echo "-----------------------------------"

    curl -X POST "$BASE_URL/api/chat" \
      -H "Content-Type: application/json" \
      -H "X-Correlation-Id: $correlation_id" \
      -d "{\"question\": \"$question\"}" \
      2>/dev/null | jq '.'

    echo ""
    echo ""
    sleep 2  # Avoid overwhelming the API
}

# Test 1: Simple factual question (should use small model)
test_request \
  "Simple Factual Question" \
  "What is AI?"

# Test 2: More specific question
test_request \
  "Specific Technical Question" \
  "What is machine learning?"

# Test 3: Question requiring inference
test_request \
  "Inference Question" \
  "How does machine learning relate to artificial intelligence?"

# Test 4: Multi-part question (should trigger escalation)
test_request \
  "Complex Multi-part Question" \
  "What is AI and how does it use machine learning, natural language processing, and computer vision?"

# Test 5: Question with specific details
test_request \
  "Detail-seeking Question" \
  "Can you explain the relationship between AI and human intelligence simulation?"

# Test 6: Comparison question
test_request \
  "Comparison Question" \
  "What are the differences between AI and machine learning?"

# Test 7: Definition question
test_request \
  "Definition Question" \
  "Define artificial intelligence in simple terms"

# Test 8: "How" question (explanatory)
test_request \
  "Explanatory How Question" \
  "How does AI simulate human intelligence?"

# Test 9: Edge case - Very short question
test_request \
  "Short Question" \
  "AI?"

# Test 10: Edge case - Question with context
test_request \
  "Question with Context" \
  "I'm new to technology. Can you explain what AI means?"

# Test 11: List/enumeration question
test_request \
  "List Question" \
  "What are the main components of AI?"

# Test 12: Application question
test_request \
  "Application Question" \
  "How is machine learning used in AI systems?"

# Test 13: Question outside knowledge base (should have low confidence)
test_request \
  "Outside Knowledge Question" \
  "What is quantum computing?"

# Test 14: Ambiguous question (should trigger retry)
test_request \
  "Ambiguous Question" \
  "Tell me about learning systems"

# Test 15: Question requiring citation
test_request \
  "Citation Required Question" \
  "According to the documentation, what is AI?"

# Test 16: Health check endpoint
echo "-----------------------------------"
echo "TEST: Health Check Endpoint"
echo "-----------------------------------"
curl -X GET "$BASE_URL/api/health" 2>/dev/null | jq '.'
echo ""
echo ""

echo "==================================="
echo "Test Suite Complete"
echo "==================================="
