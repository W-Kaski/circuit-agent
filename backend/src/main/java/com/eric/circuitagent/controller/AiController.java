package com.eric.circuitagent.controller;

import com.eric.circuitagent.agent.CircuitManus;
import com.eric.circuitagent.app.AlgorithmApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    private ChatModel dashscopeChatModel;

    @GetMapping("/algorithm_app/chat/sync")
    public String doChatWithAlgorithmAppSync(String message, String chatId) {
        return algorithmApp.doChat(message, chatId);
    }

    /**
     * SSE streaming call for Algorithm Master application
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/algorithm_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithAlgorithmAppSSE(String message, String chatId) {
        return algorithmApp.doChatByStream(message, chatId);
    }

    /**
     * SSE streaming call for Algorithm Master application (ServerSentEvent)
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/algorithm_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithAlgorithmAppServerSentEvent(String message, String chatId) {
        return algorithmApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE streaming call for Algorithm Master application (SseEmitter)
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/algorithm_app/chat/sse_emitter")
    public SseEmitter doChatWithAlgorithmAppServerSseEmitter(String message, String chatId) {
        // Create a SseEmitter with a longer timeout (3 minutes)
        SseEmitter sseEmitter = new SseEmitter(180000L);
        // Subscribe to the Flux response stream and push to SseEmitter
        algorithmApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        // Return
        return sseEmitter;
    }

    /**
     * Streaming call for Manus Super Intelligent Agent
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        CircuitManus circuitManus = new CircuitManus(allTools, dashscopeChatModel);
        return circuitManus.runStream(message);
    }

}