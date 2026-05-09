package com.eric.circuitagent.controller;

import com.eric.circuitagent.agent.CircuitManus;
import com.eric.circuitagent.app.AlgorithmApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private AlgorithmApp algorithmApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    @org.springframework.beans.factory.annotation.Qualifier("activeChatModel")
    private ChatModel activeChatModel;

    @GetMapping("/algorithm_app/chat/sync")
    public String doChatWithAlgorithmAppSync(String message, String chatId) {
        return algorithmApp.doChat(message, chatId);
    }

    @GetMapping(value = "/algorithm_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithAlgorithmAppSSE(String message, String chatId) {
        return algorithmApp.doChatByStream(message, chatId);
    }

    @GetMapping(value = "/algorithm_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithAlgorithmAppServerSentEvent(String message, String chatId) {
        return algorithmApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    @GetMapping(value = "/algorithm_app/chat/sse_emitter")
    public SseEmitter doChatWithAlgorithmAppServerSseEmitter(String message, String chatId) {
        SseEmitter sseEmitter = new SseEmitter(180000L);
        algorithmApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * Frontend-facing SSE endpoint: GET /api/ai/chat/algorithm?query=...&sessionId=...
     */
    @GetMapping(value = "/chat/algorithm", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatAlgorithm(String query, String sessionId) {
        SseEmitter sseEmitter = new SseEmitter(180000L);
        algorithmApp.doChatByStream(query, sessionId)
                .subscribe(chunk -> {
                    try {
                        // Send as JSON to prevent EventSource from stripping leading spaces
                        sseEmitter.send(java.util.Map.of("text", chunk));
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

    private final java.util.Map<String, CircuitManus> manusAgentSessionMap = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Frontend-facing Manus SSE endpoint: GET /api/ai/chat/manus?query=...&sessionId=...
     */
    @GetMapping("/chat/manus")
    public SseEmitter chatManus(String query, @RequestParam(required = false, defaultValue = "default") String sessionId) {
        CircuitManus circuitManus = manusAgentSessionMap.computeIfAbsent(sessionId, 
                k -> new CircuitManus(allTools, activeChatModel));
        return circuitManus.runStream(query);
    }

    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        CircuitManus circuitManus = new CircuitManus(allTools, activeChatModel);
        return circuitManus.runStream(message);
    }
}