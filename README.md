# Circuit Agent

> A Java-first Spring AI platform for building RAG chatbots, tool-calling agents, SSE streaming, and MCP integration — all in one monorepo.

[![Backend](https://img.shields.io/badge/backend-Spring%20Boot%203.4.7-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)](#)
[![Frontend](https://img.shields.io/badge/frontend-Vue%203%20%2B%20Vite-42b883?style=flat-square)](https://vuejs.org)
[![AI](https://img.shields.io/badge/AI-Spring%20AI%201.0%20%2B%20DashScope-blueviolet?style=flat-square)](https://docs.spring.io/spring-ai/reference/)
[![Vector Store](https://img.shields.io/badge/vector--store-PgVector-336791?style=flat-square)](#)
[![MCP](https://img.shields.io/badge/MCP-Spring%20AI%20MCP%20Client%2FServer-1a6cf5?style=flat-square)](#)
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)
[![PRs](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](./CONTRIBUTING.md)

[中文](./README.zh.md) · [Architecture](#architecture) · [Quickstart](#quickstart) · [Features](#core-capabilities) · [API Docs](#api-documentation)

---

## Overview

**Circuit Agent** is a full-stack AI experimentation platform built on Spring AI and Alibaba DashScope. It explores two distinct application patterns in a single codebase:

- **Algorithm Master** — a RAG-powered chat assistant for algorithm and data-structure questions, backed by a PgVector vector store, multi-turn `InMemory` chat memory, query rewriting, and structured output reports
- **EkManus Super Agent** — a `ReAct / ToolCallAgent` execution loop that plans tool use, invokes built-in tools (web search, web scraping, file I/O, terminal, PDF generation), and terminates gracefully via a dedicated `TerminateTool`

The project intentionally stays experimental — it's a developer sandbox for Java-first AI patterns, not a production-hardened framework.

---

## Architecture

```
Browser
  → Vue 3 SPA (Vue Router, Axios, @vueuse/head)
  → HTTP GET + SSE (Flux<String> / SseEmitter)
Spring Boot 3.4.7 backend (Java 21)        [port 8123, context /api]
  → Spring AI 1.0            — ChatClient, Advisors, ToolCallbacks
  → Spring AI Alibaba 1.0    — DashScope chat model (qwen-plus default)
  → Spring AI MCP Client     — SSE or stdio connections to MCP servers
  → PgVector (manual)        — vector store for RAG knowledge retrieval
  → Ollama (optional)        — local model support
  → Knife4j / OpenAPI 3      — interactive API docs at /api/doc.html
image-search-mcp-server (optional sidecar)  [Spring Boot 3.5.5, Java 21]
  → Spring AI MCP Server (WebMVC, stdio mode)
  → ImageSearchTool          — Pexels API-backed image search
  → Also wirable via stdio from mcp-servers.json
```

### Agent Architecture

```
BaseAgent  (step loop, state machine: IDLE → RUNNING → FINISHED/ERROR)
  └── ReActAgent   (think/act interface)
        └── ToolCallAgent   (Spring AI ToolCallingManager, manual tool execution)
              └── EkManus   (20-step limit, all built-in tools wired)
```

### RAG Pipeline (Algorithm Master)

```
User query
  → QueryRewriter (LLM-based query rewriting)
  → QuestionAnswerAdvisor (PgVector similarity search)
  → MessageChatMemoryAdvisor (20-message sliding window)
  → MyLoggerAdvisor (request/response logging)
  → DashScope LLM (qwen-plus)
  → Streaming response (Flux<String> or SseEmitter)
```

---

## Core Capabilities

| Feature | Detail |
|---------|--------|
| **RAG over Markdown** | Algorithm and data-structure docs loaded from `resources/documents/`, embedded into PgVector via `AlgorithmAppDocumentLoader` |
| **Query rewriting** | LLM rewrites user queries before retrieval for better semantic match |
| **Chat memory** | `InMemoryChatMemoryRepository` with 20-message sliding window; `FileBasedChatMemory` available |
| **Streaming** | Three SSE patterns: `Flux<String>`, `Flux<ServerSentEvent<String>>`, `SseEmitter` |
| **Structured output** | `AlgorithmReport` record generated via `.entity()` with JSON schema (victools `jsonschema-generator`) |
| **Tool-calling agent** | `ToolCallAgent` manually manages tool dispatch via `ToolCallingManager`, bypassing Spring AI auto-invocation |
| **Built-in tools** | `WebSearchTool`, `WebScrapingTool` (Jsoup), `FileOperationTool`, `ResourceDownloadTool`, `TerminalOperationTool`, `PDFGenerationTool` (iText 9), `TerminateTool` |
| **MCP integration** | Backend acts as MCP **client** — connects to external MCP servers via SSE or stdio (`mcp-servers.json`) |
| **MCP sidecar** | `image-search-mcp-server` exposes `ImageSearchTool` (Pexels) as a standalone MCP server in stdio mode |
| **Multi-model support** | DashScope (default), Ollama (local), LangChain4j DashScope community adapter |
| **API docs** | Knife4j OpenAPI 3 UI at `http://localhost:8123/api/doc.html` |

---

## Repository Layout

```
circuit-agent/
├── backend/                              # Spring Boot 3.4.7, Java 21
│   ├── src/main/java/com/eric/ekaiagent/
│   │   ├── advisors/                     # MyLoggerAdvisor (request/response logging)
│   │   ├── agent/
│   │   │   ├── BaseAgent.java            # Step-loop state machine (IDLE→RUNNING→FINISHED)
│   │   │   ├── ReActAgent.java           # think() / act() interface
│   │   │   ├── ToolCallAgent.java        # Spring AI ToolCallingManager, manual dispatch
│   │   │   └── EkManus.java             # Concrete agent, 20-step limit, all tools wired
│   │   ├── app/
│   │   │   └── AlgorithmApp.java         # Algorithm Master — chat, RAG, tools, MCP modes
│   │   ├── chatmemory/
│   │   │   └── FileBasedChatMemory.java  # File-backed chat memory (alternative to InMemory)
│   │   ├── config/                       # CORS, model config
│   │   ├── constant/                     # Shared literals
│   │   ├── controller/
│   │   │   └── AiController.java         # REST + SSE endpoints (/ai/**)
│   │   ├── rag/                          # QueryRewriter, DocumentLoader, VectorStore configs
│   │   └── tools/                        # All 8 built-in tool implementations + ToolRegistration
│   └── src/main/resources/
│       ├── application.yml               # Active profile: local; model: qwen-plus
│       ├── mcp-servers.json              # stdio MCP server wiring (amap, image-search)
│       └── documents/                    # RAG knowledge base (Algorithm.md, DataStructure.md)
│
├── frontend/                             # Vue 3, Vite 4, JavaScript
│   ├── src/
│   │   ├── views/
│   │   │   ├── Home.vue                  # Landing page
│   │   │   ├── AlgorithmMaster.vue       # RAG chat UI with SSE streaming
│   │   │   └── SuperAgent.vue            # EkManus agent UI with step-by-step output
│   │   ├── api/                          # Axios API clients
│   │   ├── router/index.js               # Vue Router 4 routes
│   │   └── components/                   # Shared UI components
│   ├── Dockerfile                        # Production image (nginx)
│   └── nginx.conf                        # Proxy config — backend:8123
│
└── image-search-mcp-server/              # Spring Boot 3.5.5, Java 21 (MCP sidecar)
    └── src/main/java/com/eric/imagesearchmcpserver/
        ├── ImageSearchMcpServerApplication.java
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
# API:      http://localhost:8123/api
# API docs: http://localhost:8123/api/doc.html
```

### 3. Run Frontend

```bash
cd frontend
npm install
npm run dev
# App: http://localhost:3000
```

### 4. (Optional) Build & Run MCP Sidecar

The image-search MCP server runs in stdio mode, launched automatically by the backend via `mcp-servers.json` when configured. To build it manually:

```bash
cd image-search-mcp-server
./mvnw -DskipTests package
# Output: target/image-search-mcp-server-0.0.1-SNAPSHOT.jar
```

To enable it in the backend, edit `mcp-servers.json` and uncomment/configure the MCP client in `application.yml`:

```yaml
spring:
  ai:
    mcp:
      client:
        stdio:
          servers-configuration: classpath:mcp-servers.json
```

### Available Frontend Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Dev server on port 3000 |
| `npm run build` | Production build |
| `npm run preview` | Preview production build locally |

---

## API Documentation

Once the backend is running, interactive docs via **Knife4j**:

```
http://localhost:8123/api/doc.html
```

Key endpoints:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/ai/algorithm_app/chat/sync` | GET | Algorithm Master — synchronous response |
| `/ai/algorithm_app/chat/sse` | GET | Algorithm Master — `Flux<String>` SSE stream |
| `/ai/algorithm_app/chat/server_sent_event` | GET | Algorithm Master — `Flux<ServerSentEvent>` |
| `/ai/algorithm_app/chat/sse_emitter` | GET | Algorithm Master — `SseEmitter` stream |
| `/ai/manus/chat` | GET | EkManus Super Agent — `SseEmitter` with step output |

---

## Status & Known Notes

- Package names still use the legacy `com.eric.ekaiagent` namespace (originally `ek-ai-agent`)
- PgVector is wired manually via `spring-boot-starter-jdbc` + `spring-ai-pgvector-store`; the auto-starter is commented out
- `AlgorithmApp.doChatWithReport()` has a minor bug: `ChatMemory.CONVERSATION_ID` param is set twice in the same advisors spec (second call overwrites the first with a literal `10`)
- MCP SSE client config is commented out in `application.yml`; only stdio mode is active by default
- Frontend has no TypeScript — uses plain JavaScript with Vite 4

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
