package com.eric.circuitagent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class ChatModelConfig {

    @Value("${OLLAMA_BASE_URL:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Value("${OLLAMA_CHAT_MODEL:qwen3:4b-instruct}")
    private String ollamaChatModel;

    @Value("${SPRING_AI_DASHSCOPE_API_KEY:}")
    private String dashscopeApiKey;

    /**
     * Provides a single "activeChatModel" bean.
     * Uses Ollama by default; falls back to Ollama even if DashScope key looks valid,
     * since we know the local Ollama is available.
     * To switch to DashScope: set USE_OLLAMA_CHAT=false and supply a real key.
     */
    @Bean
    @Primary
    public ChatModel activeChatModel(
            OllamaChatModel ollamaChatModel) {
        log.info("Using Ollama chat model at {} with model {}", ollamaBaseUrl, this.ollamaChatModel);
        return ollamaChatModel;
    }
}
