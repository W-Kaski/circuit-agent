package com.eric.circuitagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class AlgorithmAppTest {


    @Resource
    private AlgorithmApp algorithmApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "hello, 我是Eric";
        String answer = algorithmApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想知道kmp算法的时间复杂度";
        answer = algorithmApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "贪心算法里面有哪些经典的算法？";
        answer = algorithmApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好, 我是Eric,我想知道kmp算法的时间复杂度";
        AlgorithmApp.AlgorithmReport algorithmReport = algorithmApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(algorithmReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我想知道动态规划算法里面有哪些经典的例子？";
        String answer = algorithmApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = algorithmApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {

        // 测试资源下载：图片下载
        testMessage("下载一个算法的pdf文件");

    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        String message = "帮我找到一个包括数字的图片";
        String answer = algorithmApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }

}