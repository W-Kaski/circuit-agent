# Circuit Agent

> A Java-first Spring AI playground for retrieval-augmented chat, tool-calling agents, streaming UX, and MCP integration.

## Overview

`Circuit Agent` is a full-stack experimental AI platform built to explore two practical application patterns in the same codebase:

- **Algorithm Master** — a focused RAG assistant for algorithm and data-structure questions
- **Circuit Agent runtime** — a tool-calling agent loop with streaming execution output

The project is positioned as a developer platform and experiment ground, not as a production-hardened agent framework.

## Architecture

```text
Vue frontend
  -> HTTP / SSE
Spring Boot backend
  -> chat endpoints
  -> RAG pipeline
  -> tool-calling agent loop
  -> MCP client integration
Optional MCP sidecar
  -> image search server
```

## Repository Layout

- `src/` — Spring Boot application, RAG pipeline, tools, and agent runtime
- `frontend/` — Vue client for chat and streaming interaction
- `ek-image-search-mcp-server/` — standalone MCP server module for image search

## Core Capabilities

- **RAG over markdown** with chat memory and vector retrieval
- **Tool-calling agent execution** with bounded step loops
- **Streaming responses** through SSE and Spring reactive primitives
- **MCP client integration** for external tools and sidecar workflows
- **Java-first AI experimentation** using Spring AI, DashScope, and optional PgVector

## Quickstart

### Backend

```bash
./mvnw spring-boot:run
```

Default local API: `http://localhost:8123/api`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Documentation

- Portfolio/deep technical writeup: `README_portfolio.md`
- Internal technical report: `circuit-agent_技术复盘报告.md.resolved`
- Security policy: `SECURITY.md`

## Status

- The repo is intentionally experimental.
- Naming and packaging are now aligned around `circuit-agent`, but Java package names remain unchanged.
- Backend and frontend both build locally; API credentials are still expected through environment-specific configuration.

## Contributing

See `CONTRIBUTING.md`.

## License

This repository is licensed under MIT. See `LICENSE`.
