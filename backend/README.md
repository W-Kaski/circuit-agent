# Backend

Spring Boot service for the `circuit-agent` runtime.

## Responsibilities

- expose chat and streaming endpoints
- run the algorithm-focused RAG flow
- orchestrate tool-calling agent execution
- load optional MCP client configuration

## Structure

- `src/main/java/com/eric/ekaiagent/app/` — scenario entrypoints
- `src/main/java/com/eric/ekaiagent/agent/` — agent loop and tool orchestration
- `src/main/java/com/eric/ekaiagent/rag/` — retrieval pipeline and vector store setup
- `src/main/java/com/eric/ekaiagent/tools/` — tool implementations
- `src/main/resources/` — runtime config, documents, and MCP server wiring

## Run

```bash
./mvnw spring-boot:run
```

Default base URL: `http://localhost:8123/api`
