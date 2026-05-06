# Circuit Agent（AI 智能体平台）

> Java 优先的 Spring AI 实验平台，集 RAG 问答、工具调用智能体、SSE 流式输出与 MCP 集成于一体。

[![后端](https://img.shields.io/badge/后端-Spring%20Boot%203.4.7-brightgreen?style=flat-square)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)](#)
[![前端](https://img.shields.io/badge/前端-Vue%203%20%2B%20Vite-42b883?style=flat-square)](https://vuejs.org)
[![AI](https://img.shields.io/badge/AI-Spring%20AI%201.0%20%2B%20DashScope-blueviolet?style=flat-square)](https://docs.spring.io/spring-ai/reference/)
[![向量存储](https://img.shields.io/badge/向量存储-PgVector-336791?style=flat-square)](#)
[![MCP](https://img.shields.io/badge/MCP-Spring%20AI%20MCP%20客户端%2F服务端-1a6cf5?style=flat-square)](#)
[![许可证](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)
[![PRs](https://img.shields.io/badge/PRs-欢迎-brightgreen?style=flat-square)](./CONTRIBUTING.md)

[English](./README.md) · [架构](#架构) · [快速开始](#快速开始) · [核心功能](#核心功能) · [接口文档](#接口文档)

---

## 概览

**Circuit Agent** 是基于 Spring AI 和阿里云 DashScope 构建的全栈 AI 实验平台，在同一套代码库中探索两种不同的应用模式：

- **Algorithm Master（算法大师）** —— RAG 驱动的算法与数据结构问答助手，底层集成 PgVector 向量存储、多轮 `InMemory` 对话记忆、查询重写，以及结构化输出报告
- **EkManus 超级智能体** —— 基于 `ReAct / ToolCallAgent` 执行循环，自主规划工具调用，支持内置工具（网页搜索、网页抓取、文件操作、终端执行、PDF 生成），并通过专用 `TerminateTool` 优雅终止

本项目定位为开发者实验平台，而非生产就绪的 Agent 框架，旨在探索 Java 优先的 AI 工程模式。

---

## 架构

```
浏览器
  → Vue 3 SPA（Vue Router、Axios、@vueuse/head）
  → HTTP GET + SSE（Flux<String> / SseEmitter）
Spring Boot 3.4.7 后端（Java 21）           [端口 8123，上下文路径 /api]
  → Spring AI 1.0            — ChatClient、Advisors、ToolCallbacks
  → Spring AI Alibaba 1.0    — DashScope 对话模型（默认 qwen-plus）
  → Spring AI MCP Client     — 通过 SSE 或 stdio 连接外部 MCP 服务
  → PgVector（手动集成）      — RAG 知识检索向量存储
  → Ollama（可选）            — 本地模型支持
  → Knife4j / OpenAPI 3      — 交互式接口文档，访问 /api/doc.html
image-search-mcp-server（可选 sidecar）     [Spring Boot 3.5.5, Java 21]
  → Spring AI MCP Server（WebMVC，stdio 模式）
  → ImageSearchTool          — 基于 Pexels API 的图片搜索工具
  → 支持通过 mcp-servers.json 以 stdio 方式接入
```

### 智能体架构

```
BaseAgent  （步骤循环 + 状态机：IDLE → RUNNING → FINISHED/ERROR）
  └── ReActAgent   （think/act 接口定义）
        └── ToolCallAgent   （Spring AI ToolCallingManager，手动工具分发）
              └── EkManus   （最大 20 步，所有内置工具已注入）
```

### RAG 管道（Algorithm Master）

```
用户提问
  → QueryRewriter（LLM 查询重写）
  → QuestionAnswerAdvisor（PgVector 相似度检索）
  → MessageChatMemoryAdvisor（20 条消息滑动窗口）
  → MyLoggerAdvisor（请求/响应日志）
  → DashScope LLM（qwen-plus）
  → 流式响应（Flux<String> 或 SseEmitter）
```

---

## 核心功能

| 功能 | 说明 |
|------|------|
| **Markdown RAG** | 算法与数据结构文档从 `resources/documents/` 加载，通过 `AlgorithmAppDocumentLoader` 嵌入 PgVector |
| **查询重写** | LLM 在检索前重写用户查询，提升语义匹配效果 |
| **对话记忆** | `InMemoryChatMemoryRepository` + 20 条消息滑动窗口；可选 `FileBasedChatMemory` 持久化 |
| **流式响应** | 三种 SSE 模式：`Flux<String>`、`Flux<ServerSentEvent<String>>`、`SseEmitter` |
| **结构化输出** | 通过 `.entity()` 生成 `AlgorithmReport` 记录，基于 victools `jsonschema-generator` |
| **工具调用智能体** | `ToolCallAgent` 通过 `ToolCallingManager` 手动管理工具分发，绕过 Spring AI 自动调用机制 |
| **内置工具** | `WebSearchTool`、`WebScrapingTool`（Jsoup）、`FileOperationTool`、`ResourceDownloadTool`、`TerminalOperationTool`、`PDFGenerationTool`（iText 9）、`TerminateTool` |
| **MCP 集成** | 后端作为 MCP **客户端**，通过 SSE 或 stdio（`mcp-servers.json`）连接外部 MCP 服务 |
| **MCP Sidecar** | `image-search-mcp-server` 将 `ImageSearchTool`（Pexels）以独立 MCP 服务形式通过 stdio 暴露 |
| **多模型支持** | DashScope（默认）、Ollama（本地）、LangChain4j DashScope 社区适配器 |
| **接口文档** | Knife4j OpenAPI 3 界面，访问 `http://localhost:8123/api/doc.html` |

---

## 目录结构

```
circuit-agent/
├── backend/                              # Spring Boot 3.4.7，Java 21
│   ├── src/main/java/com/eric/ekaiagent/
│   │   ├── advisors/                     # MyLoggerAdvisor（请求/响应日志）
│   │   ├── agent/
│   │   │   ├── BaseAgent.java            # 步骤循环状态机（IDLE→RUNNING→FINISHED）
│   │   │   ├── ReActAgent.java           # think() / act() 接口定义
│   │   │   ├── ToolCallAgent.java        # Spring AI ToolCallingManager，手动工具分发
│   │   │   └── EkManus.java             # 具体智能体，最大 20 步，所有工具已注入
│   │   ├── app/
│   │   │   └── AlgorithmApp.java         # Algorithm Master — 对话、RAG、工具、MCP 四种模式
│   │   ├── chatmemory/
│   │   │   └── FileBasedChatMemory.java  # 基于文件的对话记忆（InMemory 的替代方案）
│   │   ├── config/                       # CORS、模型配置
│   │   ├── constant/                     # 公共常量
│   │   ├── controller/
│   │   │   └── AiController.java         # REST + SSE 接口（/ai/**）
│   │   ├── rag/                          # QueryRewriter、DocumentLoader、VectorStore 配置
│   │   └── tools/                        # 8 个内置工具实现 + ToolRegistration
│   └── src/main/resources/
│       ├── application.yml               # 激活 local profile；模型：qwen-plus
│       ├── mcp-servers.json              # stdio MCP 服务配置（amap、image-search）
│       └── documents/                    # RAG 知识库（Algorithm.md、DataStructure.md）
│
├── frontend/                             # Vue 3，Vite 4，JavaScript
│   ├── src/
│   │   ├── views/
│   │   │   ├── Home.vue                  # 首页
│   │   │   ├── AlgorithmMaster.vue       # RAG 对话 UI，支持 SSE 流式输出
│   │   │   └── SuperAgent.vue            # EkManus 智能体 UI，逐步展示执行输出
│   │   ├── api/                          # Axios API 客户端
│   │   ├── router/index.js               # Vue Router 4 路由定义
│   │   └── components/                   # 公共 UI 组件
│   ├── Dockerfile                        # 生产镜像（基于 nginx）
│   └── nginx.conf                        # 反向代理配置，后端地址为 backend:8123
│
└── image-search-mcp-server/              # Spring Boot 3.5.5，Java 21（MCP Sidecar）
    └── src/main/java/com/eric/imagesearchmcpserver/
        ├── ImageSearchMcpServerApplication.java
        └── tools/
            └── ImageSearchTool.java      # Pexels API 图片搜索，作为 MCP 工具暴露
```

---

## 快速开始

### 环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| Java | 21+ | 后端与 MCP Sidecar |
| Maven（或 `./mvnw`） | 3.9+ | 项目已内置 Wrapper |
| Node.js | 18+ | 前端 |
| PostgreSQL + pgvector | 14+ | RAG 向量存储 |
| DashScope API Key | — | 阿里云百炼大模型 |
| Pexels API Key | — | MCP Sidecar 图片搜索（可选） |

### 1. 后端配置

创建 `backend/src/main/resources/application-local.yml`（不提交到 Git）：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/circuit_agent
    username: 数据库用户名
    password: 数据库密码
  ai:
    dashscope:
      api-key: 你的_DASHSCOPE_API_KEY

search-api:
  api-key: 你的_SEARCH_API_KEY    # 供 WebSearchTool 使用
```

### 2. 启动后端

```bash
cd backend
./mvnw spring-boot:run
# API 地址：  http://localhost:8123/api
# 接口文档：  http://localhost:8123/api/doc.html
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
# 应用地址：http://localhost:3000
```

### 4. （可选）构建并运行 MCP Sidecar

Image Search MCP Server 默认以 stdio 模式运行，由后端通过 `mcp-servers.json` 自动启动。如需手动构建：

```bash
cd image-search-mcp-server
./mvnw -DskipTests package
# 输出：target/image-search-mcp-server-0.0.1-SNAPSHOT.jar
```

如需在后端启用，编辑 `mcp-servers.json` 并在 `application.yml` 中取消注释 MCP 客户端配置：

```yaml
spring:
  ai:
    mcp:
      client:
        stdio:
          servers-configuration: classpath:mcp-servers.json
```

### 前端脚本说明

| 命令 | 说明 |
|------|------|
| `npm run dev` | 开发模式（端口 3000） |
| `npm run build` | 生产构建 |
| `npm run preview` | 本地预览生产构建 |

---

## 接口文档

后端启动后，通过 **Knife4j** 访问交互式接口文档：

```
http://localhost:8123/api/doc.html
```

核心接口：

| 接口路径 | 方法 | 说明 |
|----------|------|------|
| `/ai/algorithm_app/chat/sync` | GET | 算法大师 — 同步响应 |
| `/ai/algorithm_app/chat/sse` | GET | 算法大师 — `Flux<String>` SSE 流 |
| `/ai/algorithm_app/chat/server_sent_event` | GET | 算法大师 — `Flux<ServerSentEvent>` |
| `/ai/algorithm_app/chat/sse_emitter` | GET | 算法大师 — `SseEmitter` 流 |
| `/ai/manus/chat` | GET | EkManus 超级智能体 — `SseEmitter` 逐步输出 |

---

## 状态说明与已知问题

- 包名仍使用历史遗留的 `com.eric.ekaiagent` 命名空间（原项目名为 `ek-ai-agent`）
- PgVector 通过 `spring-boot-starter-jdbc` + `spring-ai-pgvector-store` 手动集成；自动集成 Starter 已注释
- `AlgorithmApp.doChatWithReport()` 存在 **已知 Bug**：同一个 `advisors(spec ->)` 中 `ChatMemory.CONVERSATION_ID` 被设置了两次，第二次传入字面量 `10`（整数），覆盖了正确的 `chatId`
- MCP SSE 客户端配置在 `application.yml` 中已注释掉，默认仅 stdio 模式生效
- 前端使用纯 JavaScript，未引入 TypeScript

---

## 贡献

参见 [CONTRIBUTING.md](./CONTRIBUTING.md)。

## 安全

参见 [SECURITY.md](./SECURITY.md)。

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](./LICENSE)。

---

## 作者

**Eric Wang** — [个人主页](https://ek-flowity.site) · [LinkedIn](https://linkedin.com/in/-ericwang-) · [邮箱](mailto:ericwang7717@gmail.com)
