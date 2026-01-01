# Use Cases and Integration Examples

This document describes practical use cases for the AI Control Plane and provides integration examples.

## 🎯 Core Use Cases

### 1. Enterprise Knowledge Base

**Scenario:** Company has 1000+ internal documents (policies, procedures, technical specs)

**Benefits:**
- Instant answers to employee questions
- Reduced HR/IT support tickets
- Consistent information across teams
- Audit trail via citations

**Example Questions:**
```bash
"What is our remote work policy?"
"How do I request PTO?"
"What are the steps for expense reimbursement?"
```

**Implementation:**
1. Ingest company documents into PostgreSQL
2. Generate embeddings for all content
3. Expose API to internal portal or Slack bot
4. Track usage and refine based on common queries

---

### 2. Technical Documentation Assistant

**Scenario:** Software product with extensive API documentation

**Benefits:**
- Developers get instant answers
- Reduces time searching through docs
- Provides code examples with context
- Links to source documentation (citations)

**Example Questions:**
```bash
"How do I authenticate with the API?"
"What are the rate limits for the REST API?"
"Show me an example of creating a user via the API"
```

**Integration Example (Python):**
```python
import requests

def ask_docs(question):
    response = requests.post(
        'http://localhost:8080/api/chat',
        json={'question': question},
        headers={'X-Correlation-Id': f'docs-query-{uuid.uuid4()}'}
    )

    result = response.json()
    print(f"Answer: {result['answer']}")
    print(f"Confidence: {result['confidence']}")
    print(f"\nSources:")
    for citation in result['citations']:
        print(f"  - {citation['text']}")

    return result

# Usage
ask_docs("How do I handle API errors?")
```

---

### 3. Customer Support Automation

**Scenario:** E-commerce company with product manuals and FAQs

**Benefits:**
- 24/7 automated support
- Instant answers to common questions
- Reduces support agent workload
- Escalates complex issues to humans

**Example Questions:**
```bash
"How do I reset my password?"
"What is the return policy?"
"My order hasn't arrived, what should I do?"
```

**Integration Example (Slack Bot):**
```javascript
// Slack bot integration
app.message(async ({ message, say }) => {
  if (message.text.startsWith('!ask')) {
    const question = message.text.replace('!ask', '').trim();

    const response = await fetch('http://localhost:8080/api/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Correlation-Id': message.ts
      },
      body: JSON.stringify({ question })
    });

    const result = await response.json();

    await say({
      blocks: [
        {
          type: 'section',
          text: {
            type: 'mrkdwn',
            text: `*Answer:*\n${result.answer}`
          }
        },
        {
          type: 'context',
          elements: [
            {
              type: 'mrkdwn',
              text: `Confidence: ${(result.confidence * 100).toFixed(0)}% | Model: ${result.modelUsed}`
            }
          ]
        }
      ]
    });
  }
});
```

---

### 4. Research Paper Assistant

**Scenario:** Research team with 500+ academic papers

**Benefits:**
- Quickly find relevant information across papers
- Identify connections between research topics
- Generate literature review summaries
- Track source citations for academic integrity

**Example Questions:**
```bash
"What methods were used to measure X in recent studies?"
"How does paper A's findings compare to paper B?"
"What are the main conclusions about topic X?"
```

---

### 5. Legal Document Analysis

**Scenario:** Law firm with contracts, case law, and regulations

**Benefits:**
- Fast contract clause lookup
- Compliance checking against regulations
- Case law precedent finding
- **Full citation tracking for legal validity**

**Example Questions:**
```bash
"What are the termination clauses in the vendor contract?"
"Are there any force majeure provisions?"
"What regulations apply to data retention?"
```

**Note:** Legal use requires 100% verified citations - this app's verification system is ideal for this.

---

### 6. Code Documentation Q&A

**Scenario:** Large codebase with extensive inline documentation

**Benefits:**
- New developers onboard faster
- Understand legacy code quickly
- Find usage examples
- Discover related functions/modules

**Example Questions:**
```bash
"How do I use the authentication middleware?"
"What does the UserService class do?"
"Show me examples of error handling in the codebase"
```

**Integration Example (VS Code Extension):**
```typescript
// VS Code extension command
vscode.commands.registerCommand('askCodeDocs', async () => {
  const question = await vscode.window.showInputBox({
    prompt: 'Ask a question about the codebase'
  });

  if (!question) return;

  const response = await fetch('http://localhost:8080/api/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ question })
  });

  const result = await response.json();

  // Show in webview with citations
  panel.webview.html = generateHtml(result);
});
```

---

### 7. Educational Q&A Platform

**Scenario:** Online course with video transcripts and lecture notes

**Benefits:**
- Students get instant help
- 24/7 availability
- Consistent answers
- Professors track common confusion points

**Example Questions:**
```bash
"Can you explain the concept of X from lecture 5?"
"What's the difference between Y and Z?"
"What are the key takeaways from chapter 3?"
```

---

### 8. DevOps Runbook Assistant

**Scenario:** Operations team with runbooks and incident procedures

**Benefits:**
- Quick access to procedures during incidents
- Consistent incident response
- New team members get up to speed faster
- Audit trail for compliance

**Example Questions:**
```bash
"How do I restart the production database?"
"What's the procedure for a security incident?"
"How do I scale up the web servers?"
```

---

## 🔌 Integration Patterns

### REST API Direct Integration

**Best for:** Web applications, microservices

```javascript
// Simple fetch
const askQuestion = async (question) => {
  const res = await fetch('http://localhost:8080/api/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ question })
  });
  return res.json();
};
```

---

### Chat Widget Integration

**Best for:** Customer-facing websites

```html
<script>
  function sendQuestion() {
    const question = document.getElementById('question').value;

    fetch('http://localhost:8080/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ question })
    })
    .then(res => res.json())
    .then(data => {
      document.getElementById('answer').innerHTML = `
        <p><strong>Answer:</strong> ${data.answer}</p>
        <p><em>Confidence: ${(data.confidence * 100).toFixed(0)}%</em></p>
        <details>
          <summary>Sources</summary>
          <ul>
            ${data.citations.map(c => `<li>${c.text}</li>`).join('')}
          </ul>
        </details>
      `;
    });
  }
</script>
```

---

### Batch Processing

**Best for:** Bulk document analysis, testing

```python
import asyncio
import aiohttp

async def process_questions(questions):
    async with aiohttp.ClientSession() as session:
        tasks = []
        for q in questions:
            task = session.post(
                'http://localhost:8080/api/chat',
                json={'question': q}
            )
            tasks.append(task)

        responses = await asyncio.gather(*tasks)
        return [await r.json() for r in responses]

# Usage
questions = [
    "What is AI?",
    "What is machine learning?",
    "What is deep learning?"
]

results = asyncio.run(process_questions(questions))
```

---

### CLI Tool

**Best for:** Developer tools, CI/CD scripts

```bash
#!/bin/bash
# ask-docs.sh - Command-line documentation query tool

question="$*"

if [ -z "$question" ]; then
    echo "Usage: ask-docs.sh <question>"
    exit 1
fi

response=$(curl -s -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d "{\"question\": \"$question\"}")

echo "$response" | jq -r '.answer'
echo ""
echo "Confidence: $(echo "$response" | jq -r '.confidence')"
echo "Model: $(echo "$response" | jq -r '.modelUsed')"
```

---

## 📊 Monitoring and Analytics

### Key Metrics to Track

1. **Question Volume**
   - Track correlation IDs in logs
   - Aggregate by time period

2. **Confidence Scores**
   - Monitor average confidence
   - Identify low-confidence patterns

3. **Model Usage**
   - Track which models are used most
   - Optimize for cost/performance

4. **Response Times**
   - Monitor p50, p95, p99 latencies
   - Alert on degradation

5. **Verification Status**
   - Track GROUNDED vs UNGROUNDED ratio
   - Identify knowledge gaps

### Example Log Analysis

```bash
# Count questions per hour
grep "Chat request completed" logs/app.log | \
  awk '{print $1" "$2}' | \
  cut -d: -f1 | \
  uniq -c

# Average confidence scores
grep "confidence:" logs/app.log | \
  grep -oP 'confidence: \K[0-9.]+' | \
  awk '{sum+=$1; count++} END {print sum/count}'

# Model usage distribution
grep "modelUsed" logs/app.log | \
  grep -oP 'modelUsed: \K[a-z0-9.:]+' | \
  sort | uniq -c
```

---

## 🚀 Scaling Considerations

### Horizontal Scaling

- Deploy multiple API instances behind load balancer
- PostgreSQL remains single source of truth
- Use connection pooling for database

### Caching Layer

```python
# Add Redis for frequent questions
import redis

cache = redis.Redis(host='localhost', port=6379)

def ask_with_cache(question):
    # Check cache
    cached = cache.get(question)
    if cached:
        return json.loads(cached)

    # Call API
    result = ask_api(question)

    # Cache for 1 hour
    cache.setex(question, 3600, json.dumps(result))
    return result
```

### Rate Limiting

```javascript
// Express middleware
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});

app.use('/api/chat', limiter);
```

---

## 🔐 Security Considerations

1. **Authentication**
   - Add JWT or API key authentication
   - Track usage per user/org

2. **Authorization**
   - Restrict access to specific document collections
   - Row-level security in PostgreSQL

3. **Input Validation**
   - Sanitize questions
   - Limit question length
   - Filter sensitive keywords

4. **Audit Logging**
   - Log all questions with user context
   - Track data access via citations
   - Compliance reporting

---

## 📈 ROI Calculation

**Example: Customer Support Use Case**

- Support tickets before: 1000/month
- Tickets deflected by AI: 40% (400)
- Cost per ticket: $10
- Monthly savings: $4,000
- Implementation cost: ~$10,000
- **ROI: 2.5 months**

Plus intangible benefits:
- Faster customer response
- 24/7 availability
- Consistent answers
- Scalability
