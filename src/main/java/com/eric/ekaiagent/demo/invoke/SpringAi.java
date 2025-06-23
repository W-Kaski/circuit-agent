package com.eric.ekaiagent.demo.invoke;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Use Spring Ai
 */
@Slf4j
@Component
public class SpringAi implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage message = dashscopeChatModel.call(new Prompt("说一段诗词"))
                .getResult()
                .getOutput();
        System.out.println(message.getText());
    }
}
