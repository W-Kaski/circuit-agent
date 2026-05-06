# Image Search MCP Server

Standalone Spring AI MCP server that exposes a Pexels-backed image search tool.

## Responsibilities

- run in stdio mode for local MCP client integration
- expose `ImageSearchTool` as an MCP tool callback provider
- keep image-search concerns outside the main backend runtime

## Run

```bash
./mvnw spring-boot:run
```

## Package

```bash
./mvnw -DskipTests package
```
