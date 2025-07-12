package com.eric.ekaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication // 关键注解1：标记主配置类
@Component
public class LangChainAi {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashScopeApiKey;


    public void run() {
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .apiKey(dashScopeApiKey)
                .modelName("qwen-plus")
                .build();
        String answer = qwenChatModel.chat("说一句诗");
        System.out.println(answer);
    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LangChainAi.class, args);
        LangChainAi langChainAi = context.getBean(LangChainAi.class);
        langChainAi.run();
    }
}
