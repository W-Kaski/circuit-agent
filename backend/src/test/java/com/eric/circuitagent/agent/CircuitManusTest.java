package com.eric.circuitagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CircuitManusTest {

    @Resource
    private CircuitManus circuitManus;

    @Test
    public void run() {
        String userPrompt = """ 
                can you give me a picture of a paragraph of code?""";
        String answer = circuitManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }

}