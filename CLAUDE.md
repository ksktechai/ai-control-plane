# ai-control-plane — Claude Code Instructions

You are Claude Code acting as a **senior Java 25 architect and implementation engineer**.

This repository contains **no code yet**.
Your job is to generate the project **incrementally, module by module**, strictly following the rules below.

---

## 🎯 PROJECT OVERVIEW

**Project Name:** ai-control-plane

**Base Java Package (MANDATORY):**
All Java packages MUST start with:

    com.ai

Examples:
- com.ai.api
- com.ai.control
- com.ai.rag
- com.ai.embeddings

No other root packages are allowed.

**Goal:**
Build an **enterprise-grade, agentic RAG control-plane** that uses **ONLY open-source AI models** and runs **fully locally**.

The system must:
- Use Retrieval-Augmented Generation (RAG)
- Use PostgreSQL + pgvector
- Route between multiple open-source LLMs
- Verify grounding and retry automatically
- Expose a clean REST API
- Be production-quality and compile cleanly

---

## 🧠 CORE DESIGN PRINCIPLES

1. **Open-source models ONLY**
   - Ollama-based LLMs (Llama, Qwen, Mistral, DeepSeek)
   - OSS embedding models (BGE, E5, Nomic)
   - No OpenAI, no Anthropic, no paid APIs

2. **Strict separation of concerns**
   - API ≠ AI logic
   - Control plane ≠ execution
   - Reasoning ≠ verification
   - Retrieval ≠ generation

3. **Control-Plane is the brain**
   - Decides model
   - Controls token budget
   - Controls retrieval strategy
   - Retries when answers are not grounded

4. **Everything must be observable**
   - Confidence score
   - Model used
   - Retrieval strategy
   - Verification result

---

## 🧱 TECH STACK (NON-NEGOTIABLE)

- Java **25**
- **Spring Boot 4.x** (latest stable; NOT Spring Boot 3)
- Gradle (NOT Maven)
- PostgreSQL + pgvector
- Ollama (local LLM runtime)
- Clean / Hexagonal architecture
- Docker Compose for infra

---

## 🪵 LOGGING & OBSERVABILITY (MANDATORY)

You MUST implement logging with **Log4j2** (NOT Logback).

### 1) API request/response logging
- Log **every incoming HTTP request** and **every outgoing HTTP response**.
- Include:
  - method, path, status, duration
  - correlationId (generate if absent; propagate via MDC)
  - request body and response body (see redaction rules below)

Implementation guidance:
- Use a Servlet Filter (Spring MVC) or WebFilter (WebFlux) to wrap request/response and log bodies safely.

### 2) SQL logging
- Log **every SQL statement** executed, AND the **result summary**.
- Include:
  - correlationId
  - SQL (with parameter values where possible)
  - row count / affected rows
  - query duration
- If using JDBC/JdbcTemplate:
  - Use datasource-proxy / p6spy / or a custom wrapper to capture SQL + timings.
- If using JPA:
  - Configure SQL logging + bind param logging, and still capture timing + correlationId.

### 3) RAG retrieval logging
- Log retrieval:
  - query embedding model name
  - topK
  - retrieved chunk IDs + document IDs
  - similarity scores
  - total retrieval duration

### 4) Safety / redaction rules
- Do NOT log secrets/tokens/passwords.
- Redact fields matching (case-insensitive): password, token, secret, apiKey, authorization, cookie.
- Truncate very large bodies (configurable limit).

### 5) Log format
- Use structured JSON logs if feasible (recommended), otherwise consistent key=value.
- Always include: timestamp, level, logger, correlationId, thread.

---

## ✅ TESTING (MANDATORY)

You MUST produce **JUnit 5 (Jupiter)** tests with **100% unit test coverage**.

Requirements:
- Use JUnit Jupiter + AssertJ.
- Use Mockito where appropriate.
- No integration tests counted as “unit”.
- Cover:
  - happy paths
  - error paths
  - retries
  - verification failures
  - edge cases (empty inputs, oversized context, etc.)
- Enforce coverage with **JaCoCo**:
  - minimum line coverage: 100%
  - minimum branch coverage: 100%
  - build must fail if below thresholds

Notes:
- If any class is hard to unit test, refactor it until it is testable.
- Avoid static singletons; prefer dependency injection and interfaces.

---

## 🧪 CI / GITHUB ACTIONS (MANDATORY)

You MUST create a GitHub Actions workflow that ensures the project builds correctly on every push/PR.

Workflow requirements:
- Trigger on:
  - push (all branches)
  - pull_request (all branches)
- Use:
  - ubuntu-latest
  - Java 25 (Temurin)
- Steps:
  1. checkout
  2. setup-java
  3. cache Gradle
  4. run: ./gradlew clean test jacocoTestReport
  5. run: ./gradlew check (must include JaCoCo coverage verification)
  6. run: ./gradlew docs (diagram render task; see below)
- The workflow must FAIL if:
  - tests fail
  - JaCoCo coverage thresholds are not met
  - docs/diagram generation fails

---

## 📚 DOCUMENTATION (MANDATORY)

You MUST create a **detailed README.md** and a **docs/** folder at the root.

### 1) Docs folder layout (MANDATORY)

ai-control-plane/
└── docs/
    ├── diagrams/
    │   ├── architecture.mmd
    │   ├── sequence.puml
    │   └── dataflow.mmd   (optional but recommended)
    └── images/
        ├── architecture.png
        ├── sequence.png
        └── dataflow.png   (optional)

### 2) Diagram sources
- Mermaid source MUST be in: `docs/diagrams/*.mmd`
- PlantUML source MUST be in: `docs/diagrams/*.puml`

### 3) Rendered diagram images
- Render Mermaid to PNG: `docs/images/*.png`
- Render PlantUML to PNG: `docs/images/*.png`

### 4) README requirements (MANDATORY)
README.md MUST include:
- Project overview
- Key features
- Tech stack
- Local run instructions (Docker Compose for Postgres+pgvector+Ollama)
- API usage examples (curl)
- “How it works” section (Control-plane routing + RAG + verification loop)
- **Architecture diagram embedded as an image**
- **Sequence diagram embedded as an image**
- Links to diagram source files under docs/diagrams/

README MUST embed images like:
- `![Architecture](docs/images/architecture.png)`
- `![Sequence](docs/images/sequence.png)`

### 5) Diagram generation automation (MANDATORY)
Create a Gradle task: `./gradlew docs` that:
- Generates Mermaid PNGs from `docs/diagrams/*.mmd`
- Generates PlantUML PNGs from `docs/diagrams/*.puml`
- Writes outputs to `docs/images/`

Implementation options (choose one and implement fully):
A) Use Docker in Gradle tasks:
- mermaid-cli container for Mermaid rendering
- plantuml/plantuml container for PlantUML rendering

B) Use local tool dependencies:
- mermaid-cli via Node (include package.json under docs/ or root, and wire via Gradle Exec)
- plantuml jar + graphviz (document installation)

Prefer option A (Docker) for reproducibility.

The GitHub Actions workflow MUST run `./gradlew docs` to prove diagram generation works.

---

## 📦 REQUIRED MODULES

Generate the following **Gradle multi-modules**:

ai-control-plane/
├── api/
├── control-plane/
├── llm-router/
├── embeddings/
├── rag/
├── verifier/
├── common/

### Module Responsibilities

#### api
- Spring Boot application
- REST endpoint: POST /chat
- No AI logic
- Calls ControlPlane (FlightController equivalent) only
- Package: com.ai.api

#### control-plane
- Core decision engine
- Chooses:
  - LLM
  - Token budget
  - Retrieval strategy
- Retries when verification fails
- Exposes:
  AnswerResult answer(String question);
- Package: com.ai.control

#### llm-router
- Abstraction over OSS LLMs
- Ollama-based implementations
- No business logic
- Package: com.ai.llm

#### embeddings
- Embedding generation
- OSS models only
- No Postgres logic
- Package: com.ai.embeddings

#### rag
- Document chunking
- Semantic retrieval
- pgvector queries
- Context assembly
- Package: com.ai.rag

#### verifier
- Claim extraction
- Grounding verification
- Confidence scoring
- Uses small OSS models
- Package: com.ai.verifier

#### common
- Shared DTOs
- Domain models
- Utilities
- Package: com.ai

---

## 🔁 GENERATION RULES (VERY IMPORTANT)

You MUST:
1. Generate code **incrementally**
2. Finish **one module at a time**
3. Ensure every module **compiles**
4. Output **full file contents**
5. Use **real implementations**
6. Avoid TODOs, mocks, or placeholders
7. Ask before making architectural changes

You MUST NOT:
- Generate the entire project at once
- Skip build files
- Mix responsibilities across modules
- Introduce proprietary services

---

## 🧩 GENERATION ORDER (FOLLOW EXACTLY)

1. Project structure + explanation
2. Root settings.gradle and build.gradle (include Log4j2 + JaCoCo + docs task wiring)
3. common module
4. api module (include request/response logging filter)
5. control-plane module (+ unit tests)
6. llm-router module (+ unit tests)
7. embeddings module (+ unit tests)
8. rag module (+ SQL logging + unit tests)
9. verifier module (+ unit tests)
10. docs/diagrams sources (Mermaid + PlantUML)
11. docs generation task + outputs under docs/images (generated by ./gradlew docs)
12. Docker Compose + SQL
13. GitHub Actions workflow
14. README.md (must embed docs/images/*.png)

After each step, STOP and wait for confirmation.

---

## 📌 OUTPUT FORMAT (MANDATORY)

When generating files, ALWAYS use:

--- FILE: path/to/file ---
<full file contents>

No summaries.
No explanations.
No markdown outside files.

---

## 🏁 SUCCESS CRITERIA

The final project must:
- Compile with Java 25
- Run fully offline
- Answer questions using RAG
- Verify grounding
- Retry when ungrounded
- Return confidence + citations
- Log all API requests/responses and SQL queries/results using Log4j2
- Maintain 100% unit test coverage (line + branch) enforced via JaCoCo
- Build successfully in GitHub Actions
- Provide docs/diagrams (Mermaid + PlantUML) and generated docs/images embedded in README

---

## 🚨 FINAL NOTE

If you are unsure, STOP and ASK.
Never guess.

You are writing production-grade code.
