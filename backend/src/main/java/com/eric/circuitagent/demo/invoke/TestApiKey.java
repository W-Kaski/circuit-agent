package com.eric.circuitagent.demo.invoke;

final class TestApiKey {

    static final String API_KEY = System.getenv().getOrDefault(
            "DASHSCOPE_API_KEY",
            "demo-api-key-placeholder");

    private TestApiKey() {
    }
}
