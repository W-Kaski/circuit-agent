package com.eric.circuitagent.app;

import com.eric.circuitagent.advisors.MyLoggerAdvisor;
import com.eric.circuitagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class AlgorithmApp {

        private final ChatClient chatClient;

        private static final String SYSTEM_PROMPT = "You are a senior algorithm expert, specializing in explaining and answering all kinds of algorithm and data structure questions. "
                        +
                        "Your main responsibilities include: " +
                        "- Explaining the principles, implementation, and application scenarios of common algorithms (such as sorting, searching, dynamic programming, greedy, graph theory, etc.); "
                        +
                        "- Comparing the advantages and disadvantages of different algorithms or data structures, helping users choose the appropriate solution; "
                        +
                        "- Providing clear analysis, code examples, and complexity analysis for specific algorithm problems raised by users; "
                        +
                        "- Supporting algorithm implementation questions related to programming languages and providing code references; "
                        +
                        "- Encouraging users to ask more questions and patiently guiding them to understand the core ideas of algorithms. "
                        +
                        "Please use clear and easy-to-understand language, combined with examples and code, to help users truly understand algorithm knowledge.";

        public AlgorithmApp(
                @org.springframework.beans.factory.annotation.Qualifier("activeChatModel") ChatModel chatModel) {
                MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                                .maxMessages(20)
                                .build();

                chatClient = ChatClient.builder(chatModel)
                                .defaultSystem(SYSTEM_PROMPT)
                                .defaultAdvisors(
                                                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                                                new MyLoggerAdvisor()
                                )
                                .build();
        }

        /**
         * Basic AI chat (supports multi-turn conversation)
         *
         * @param message User's question
         * @param chatId  Conversation ID
         * @return AI's answer
         */
        public String doChat(String message, String chatId) {
                ChatResponse response = chatClient
                                .prompt()
                                .user(message)
                                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                                .call()
                                .chatResponse();
                String content = response.getResult().getOutput().getText();
                log.info("content: {}", content);
                return content;
        }

        /**
         * Basic AI chat with memory and SSE streaming support.
         *
         * @param message User's question
         * @param chatId  Conversation ID
         * @return Streaming response
         */
        public Flux<String> doChatByStream(String message, String chatId) {
                String rewrittenMessage = queryRewriter != null ? queryRewriter.doQueryRewrite(message) : message;
                return chatClient
                                .prompt()
                                .user(rewrittenMessage)
                                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                                .advisors(new QuestionAnswerAdvisor(algorithmAppVectorStore))
                                .stream()
                                .content();
        }

        /**
         * Data structure for algorithm report.
         */
        record AlgorithmReport(String title, List<String> suggestions) {
        }

        /**
         * Generate an algorithm report after each conversation.
         *
         * @param message User's question
         * @param chatId  Conversation ID
         * @return Algorithm report
         */
        public AlgorithmReport doChatWithReport(String message, String chatId) {
                AlgorithmReport algorithmReport = chatClient
                                .prompt()
                                .system(SYSTEM_PROMPT + "每次对话后都要生成算法结果，标题为{用户名}的算法报告，内容为建议列表")
                                .user(message)
                                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                                                .param(ChatMemory.CONVERSATION_ID, 10))
                                .call()
                                .entity(AlgorithmReport.class);
                log.info("algorithmReport: {}", algorithmReport);
                return algorithmReport;
        }

        @Resource
        private VectorStore algorithmAppVectorStore;

        @Resource
        private VectorStore pgVectorStore;

        @Resource
        private QueryRewriter queryRewriter;

        /**
         * Chat with RAG knowledge base.
         *
         * @param message User's question
         * @param chatId  Conversation ID
         * @return AI's answer
         */
        public String doChatWithRag(String message, String chatId) {
                // 查询重写
                String rewrittenMessage = queryRewriter.doQueryRewrite(message);
                ChatResponse chatResponse = chatClient
                                .prompt()
                                // 使用改写后的查询
                                .user(rewrittenMessage)
                                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                                // 开启日志，便于观察效果
                                .advisors(new MyLoggerAdvisor())
                                // 应用 RAG 知识库问答
                                .advisors(new QuestionAnswerAdvisor(algorithmAppVectorStore))
                                // 应用 RAG 检索增强服务（基于云知识库服务）
                                // .advisors(algorithmAppRagCloudAdvisor)
                                // 应用 RAG 检索增强服务（基于 PgVector 向量存储）
                                // .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                                // 应用自定义的 RAG 检索增强服务（文档查询器 + 上下文增强器）
                                // .advisors(
                                // AlgorithmAppRagCustomAdvisorFactory.createAlgorithmAppRagCustomAdvisor(
                                // algorithmAppVectorStore, "算法"
                                // )
                                // )
                                .call()
                                .chatResponse();
                String content = chatResponse.getResult().getOutput().getText();
                log.info("content: {}", content);
                return content;
        }

        /**
         * AI tool invocation capability.
         */
        @Resource
        private ToolCallback[] allTools;

        /**
         * Algorithm report function (supports tool invocation).
         *
         * @param message User's question
         * @param chatId  Conversation ID
         * @return AI's answer
         */
        public String doChatWithTools(String message, String chatId) {
                ChatResponse chatResponse = chatClient
                                .prompt()
                                .user(message)
                                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                                // 开启日志，便于观察效果
                                .advisors(new MyLoggerAdvisor())
                                .toolCallbacks(allTools)
                                .call()
                                .chatResponse();
                String content = chatResponse.getResult().getOutput().getText();
                log.info("content: {}", content);
                return content;
        }

        @Resource
        private ToolCallbackProvider toolCallbackProvider;

        /**
         * Algorithm report function (calls MCP service).
         *
         * @param message User's question
         * @param chatId  Conversation ID
         * @return AI's answer
         */
        public String doChatWithMcp(String message, String chatId) {
                ChatResponse chatResponse = chatClient
                                .prompt()
                                .user(message)
                                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                                // 开启日志，便于观察效果
                                .advisors(new MyLoggerAdvisor())
                                .toolCallbacks(toolCallbackProvider)
                                .call()
                                .chatResponse();
                String content = chatResponse.getResult().getOutput().getText();
                log.info("content: {}", content);
                return content;
        }

}
