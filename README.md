# Circuit Agent — AI Agent Platform

> A Java-first Spring AI lab platform combining RAG Q&A, tool-calling agents, SSE streaming, and MCP integration — with a fully rebuilt React frontend for interactive demonstration.

[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.4.7-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)](#)
[![Frontend](https://img.shields.io/badge/Frontend-React%2019%20%2B%20Vite-61DAFB?style=flat-square)](https://react.dev)
[![AI](https://img.shields.io/badge/AI-Spring%20AI%201.0%20%2B%20DashScope-blueviolet?style=flat-square)](https://docs.spring.io/spring-ai/reference/)
[![Vector Store](https://img.shields.io/badge/Vector%20Store-PgVector-336791?style=flat-square)](#)
[![MCP](https://img.shields.io/badge/MCP-Spring%20AI%20MCP%20Client%2FServer-1a6cf5?style=flat-square)](#)
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)
[![PRs](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](./CONTRIBUTING.md)

[中文](./README.zh.md) · [Architecture](#architecture) · [Quickstart](#quickstart) · [Features](#core-capabilities) · [API Docs](#api-documentation)

---

## Overview

**Circuit Agent** is a full-stack AI experimentation platform built on Spring AI and Alibaba Cloud DashScope. It explores two distinct application modes within a single codebase:

- **Algorithm Master** — A RAG-powered Q&A assistant for algorithms and data structures. Backed by PgVector, multi-turn `InMemory` chat memory, query rewriting, and structured report output.
- **CircuitManus Super Agent** — An autonomous agent based on the `ReAct / ToolCallAgent` execution loop. It self-plans tool invocations, supports built-in tools (web search, scraping, file ops, terminal, PDF generation), and terminates gracefully via a dedicated `TerminateTool`. Session context is persisted across turns via a `sessionId`-keyed agent cache.

The project is designed as a developer experimentation platform, not a production-ready agent framework. Its goal is to explore Java-first AI engineering patterns.

---

## Architecture

```
Browser
  → React 19 SPA (Vite, TailwindCSS, lucide-react, react-markdown)
  → HTTP GET + SSE (EventSource / SseEmitter)
Spring Boot 3.4.7 Backend (Java 21)          [port 48124, context /api]
  → Spring AI 1.0             — ChatClient, Advisors, ToolCallbacks
  → Spring AI Alibaba 1.0     — DashScope chat model (default: qwen-plus)
  → Spring AI MCP Client      — connects to external MCP servers via SSE or stdio
  → PgVector (manual wiring)  — vector store for RAG knowledge retrieval
  → Ollama (optional)         — local model support
  → Knife4j / OpenAPI 3       — interactive API docs at /api/doc.html
image-search-mcp-server (optional sidecar)   [Spring Boot 3.5.5, Java 21]
  → Spring AI MCP Server (WebMVC, stdio mode)
  → ImageSearchTool           — Pexels API image search exposed as MCP tool
  → Wired via mcp-servers.json in stdio mode
```

### Agent Architecture

```
BaseAgent  (step loop + state machine: IDLE → RUNNING → FINISHED/ERROR)
  └── ReActAgent   (think/act interface — returns clean final answer, no step prefix)
        └── ToolCallAgent   (Spring AI ToolCallingManager, manual dispatch)
              └── CircuitManus   (20-step limit, all built-in tools wired, sessionId-keyed cache)
```

### RAG Pipeline (Algorithm Master)

```
User query
  → QueryRewriter (LLM-based query rewriting)
  → QuestionAnswerAdvisor (PgVector similarity search)
  → MessageChatMemoryAdvisor (20-message sliding window)
  → MyLoggerAdvisor (request/response logging)
  → DashScope LLM (qwen-plus)
  → Streaming response (Flux<String> / SseEmitter with [DONE] termination signal)
```

---

## Core Capabilities

| Feature | Detail |
|---------|--------|
| **RAG over Markdown** | Algorithm and data-structure docs loaded from `resources/documents/`, embedded into PgVector via `AlgorithmAppDocumentLoader` |
| **Query rewriting** | LLM rewrites user queries before retrieval for improved semantic matching |
| **Chat memory** | `InMemoryChatMemoryRepository` with 20-message sliding window; `FileBasedChatMemory` available as alternative |
| **SSE Streaming** | Robust SSE via `SseEmitter` with explicit `[DONE]` termination — prevents browser reconnection loops |
| **Multi-turn context** | `sessionId`-keyed `CircuitManus` instance cache in `AiController` for persistent cross-turn agent state |
| **Structured output** | `AlgorithmReport` record via `.entity()` with JSON schema (victools `jsonschema-generator`) |
| **Tool-calling agent** | `ToolCallAgent` manually manages dispatch via `ToolCallingManager`, bypassing Spring AI auto-invocation |
| **Built-in tools** | `WebSearchTool`, `WebScrapingTool` (Jsoup + User-Agent), `FileOperationTool`, `ResourceDownloadTool`, `TerminalOperationTool`, `PDFGenerationTool` (iText 9), `TerminateTool` |
| **MCP integration** | Backend acts as MCP **client** — connects to external MCP servers via SSE or stdio (`mcp-servers.json`) |
| **MCP sidecar** | `image-search-mcp-server` exposes `ImageSearchTool` (Pexels) as a standalone MCP server in stdio mode |
| **Multi-model support** | DashScope (default), Ollama (local), LangChain4j DashScope community adapter |
| **API docs** | Knife4j OpenAPI 3 UI at `http://localhost:48124/api/doc.html` |

### Frontend Highlights (React 19)

| Feature | Detail |
|---------|--------|
| **Multi-turn chat UI** | Message history rendered as chat bubbles with auto-scroll; React Strict Mode safe (immutable state updates) |
| **SSE streaming** | `EventSource`-based streaming with `[DONE]` signal handling and graceful connection teardown |
| **Auto Demo Mode** | On load, pings the backend. If unreachable, silently enters Demo Mode with simulated streaming and mock responses |
| **Mode-aware suggestions** | Landing page shows contextual suggested questions that switch between Local RAG and Web/Manus modes |
| **Offline Library** | Sidebar displays mock algorithm documents with full preview when backend is offline |
| **Custom SVG Logo** | Wave-in-circle brand icon consistently applied to favicon, landing page, and AI chat avatar |

---

## Repository Layout

```
circuit-agent/
├── backend/                              # Spring Boot 3.4.7, Java 21
│   ├── src/main/java/com/eric/circuitagent/
│   │   ├── advisors/                     # MyLoggerAdvisor (request/response logging)
│   │   ├── agent/
│   │   │   ├── BaseAgent.java            # Step-loop state machine (IDLE→RUNNING→FINISHED), SSE emission
│   │   │   ├── ReActAgent.java           # think() / act() interface, clean final answer output
│   │   │   ├── ToolCallAgent.java        # Spring AI ToolCallingManager, manual tool dispatch
│   │   │   └── CircuitManus.java        # Concrete agent, 20-step limit, all tools wired
│   │   ├── app/
│   │   │   └── AlgorithmApp.java         # Algorithm Master — chat, RAG, tools, MCP modes
│   │   ├── chatmemory/
│   │   │   └── FileBasedChatMemory.java  # File-backed chat memory (alternative to InMemory)
│   │   ├── config/                       # CORS, model config
│   │   ├── controller/
│   │   │   └── AiController.java         # REST + SSE endpoints (/ai/**), sessionId-keyed agent cache
│   │   ├── rag/                          # QueryRewriter, DocumentLoader, VectorStore configs
│   │   └── tools/                        # All built-in tool implementations
│   └── src/main/resources/
│       ├── application.yml               # Active profile: local; model: qwen-plus
│       ├── mcp-servers.json              # stdio MCP server wiring
│       └── documents/                    # RAG knowledge base (Markdown files)
│
├── frontend/                             # React 19, Vite, TailwindCSS, JavaScript
│   ├── src/
│   │   ├── App.jsx                       # Main app — chat logic, SSE, Demo Mode, mode switching
│   │   ├── components/
│   │   │   └── Sidebar.jsx               # Document workspace with live + offline (demo) support
│   │   └── lib/
│   │       └── api.js                    # Axios client + chatStream (EventSource)
│   ├── public/
│   │   └── favicon.svg                   # Custom wave-circle SVG brand icon
│   └── index.html                        # App entry — title: "Circuit Agent"
│
└── image-search-mcp-server/              # Spring Boot 3.5.5, Java 21 (MCP sidecar)
    └── src/main/java/com/eric/imagesearchmcpserver/
        └── tools/
            └── ImageSearchTool.java      # Pexels API image search, exposed as MCP tool
```

---

## Quickstart

### Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| Java | 21+ | Backend and MCP sidecar |
| Maven (or `./mvnw`) | 3.9+ | Wrapper included |
| Node.js | 18+ | Frontend |
| PostgreSQL + pgvector | 14+ | Vector store for RAG |
| DashScope API Key | — | Alibaba Cloud AI |
| Pexels API Key | — | MCP sidecar image search (optional) |

### 1. Backend Configuration

Create `backend/src/main/resources/application-local.yml` (not committed):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/circuit_agent
    username: your_pg_user
    password: your_pg_password
  ai:
    dashscope:
      api-key: YOUR_DASHSCOPE_API_KEY

search-api:
  api-key: YOUR_SEARCH_API_KEY    # for WebSearchTool
```

### 2. Run Backend

```bash
cd backend
./mvnw spring-boot:run
# API:      http://localhost:48124/api
# API docs: http://localhost:48124/api/doc.html
```

### 3. Run Frontend

```bash
cd frontend
npm install
npm run dev
# App: http://localhost:5173
```

> **Demo Mode**: If you skip the backend, the frontend automatically detects the offline state and enters Demo Mode with simulated streaming responses and mock documents.

### 4. (Optional) Build MCP Sidecar

```bash
cd image-search-mcp-server
./mvnw -DskipTests package
```

Enable in the backend by editing `mcp-servers.json` and configuring the MCP client in `application.yml`.

---

## API Documentation

Once the backend is running, interactive docs via **Knife4j**:

```
http://localhost:48124/api/doc.html
```

Key endpoints:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/ai/chat/algorithm` | GET | Algorithm Master — SSE streaming (RAG mode) |
| `/ai/chat/manus` | GET | CircuitManus Agent — SSE step-by-step output |
| `/knowledge/files` | GET | List uploaded knowledge base documents |
| `/knowledge/reindex` | POST | Re-index documents into PgVector |

---

## Status & Known Notes

- Package names use the legacy `com.eric.ekaiagent` namespace in some places (originally `ek-ai-agent`)
- PgVector is wired manually via `spring-boot-starter-jdbc` + `spring-ai-pgvector-store`
- `AlgorithmApp.doChatWithReport()` has a minor bug: `ChatMemory.CONVERSATION_ID` is set twice in the same advisors spec — the second call overwrites the first with a literal `10`
- MCP SSE client config is commented out in `application.yml`; only stdio mode is active by default
- Frontend uses plain JavaScript (no TypeScript)

---

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md).

## Security

See [SECURITY.md](./SECURITY.md).

## License

Licensed under MIT. See [LICENSE](./LICENSE).

---

## Author

**Eric Wang** — [Portfolio](https://ek-flowity.site) · [LinkedIn](https://linkedin.com/in/-ericwang-) · [Email](mailto:ericwang7717@gmail.com)
