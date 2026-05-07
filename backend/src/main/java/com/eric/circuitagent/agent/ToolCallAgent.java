package com.eric.circuitagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.eric.circuitagent.agent.model.AgentState;
import lombok.Data;

import cn.hutool.core.util.StrUtil;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.chat.messages.UserMessage;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;

/**
 * Base agent class for handling tool invocation, implements think and act
 * methods, can be used as a parent class for instantiation.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // Available tools
    private final ToolCallback[] availableTools;

    // Store the response result of tool invocation (which tools to call)
    private ChatResponse toolCallChatResponse;

    // Tool invocation manager
    private final ToolCallingManager toolCallingManager;

    // Disable Spring AI's built-in tool invocation mechanism, manage options and
    // message context manually
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // Disable Spring AI's built-in tool invocation mechanism, manage options and
        // message context manually
        this.chatOptions = DashScopeChatOptions.builder()
                .withInternalToolExecutionEnabled(false)
                .build();
    }

    /**
     * Handle the current state and decide the next action
     *
     * @return Whether to execute an action
     */
    @Override
    public boolean think() {
        // 1. Validate prompt, concatenate user prompt
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        // 2. Call LLM, get tool invocation result
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, this.chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
            // Record response, used for next Act
            this.toolCallChatResponse = chatResponse;
            // 3. Parse tool invocation result, get tools to call
            // Assistant message
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // Get the list of tools to call
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            // Output prompt information
            String result = assistantMessage.getText();
            log.info(getName() + "'s thinking: " + result);
            log.info(getName() + " selected " + toolCallList.size() + " tools to use");
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("Tool name: %s, arguments: %s", toolCall.name(),
                            toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            // If no tools need to be called, return false
            if (toolCallList.isEmpty()) {
                // Only when not calling tools, manually record assistant message
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // When calling tools, no need to record assistant message, as it will be
                // recorded automatically
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "'s thinking process encountered a problem: " + e.getMessage());
            getMessageList().add(new AssistantMessage("Error encountered during processing: " + e.getMessage()));
            return false;
        }
    }

    /**
     * Execute tool invocation and handle the result
     *
     * @return Execution result
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return "No tools need to be called";
        }
        // Call tools
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // Record message context, conversationHistory already contains assistant
        // messages and tool invocation results
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil
                .getLast(toolExecutionResult.conversationHistory());
        // Determine if the terminate tool was called
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if (terminateToolCalled) {
            // Task finished, update state
            setState(AgentState.FINISHED);
        }
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "Tool " + response.name() + " returned: " + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);
        return results;
    }
}