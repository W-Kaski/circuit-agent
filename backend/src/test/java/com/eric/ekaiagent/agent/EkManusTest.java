package com.eric.ekaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EkManusTest {

    @Resource
    private EkManus ekManus;

    @Test
    public void run() {
        String userPrompt = """ 
                can you give me a picture of a paragraph of code?""";
        String answer = ekManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }

}