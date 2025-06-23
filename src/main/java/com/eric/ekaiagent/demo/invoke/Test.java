package com.eric.ekaiagent.demo.invoke;

public class Test {
    public static void main(String[] args) {
        HttpAi H = new HttpAi();
        String result = H.chatWithQwen("hello 你是谁");
        System.out.println(result);
    }
}
