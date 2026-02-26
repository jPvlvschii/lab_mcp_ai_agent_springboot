package com.example.agent.config;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import com.example.agent.BacklogAgent;
import com.example.agent.tools.AgentTool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.List;

@Configuration
public class LangChainConfig {

    @Bean
    @Profile("!ci")
    public AnthropicChatModel anthropicChatModel(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model}") String model,
            @Value("${anthropic.max-tokens:800}") Integer maxTokens,
            @Value("${anthropic.timeout-seconds:60}") Integer timeoutSeconds) {
        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .maxTokens(maxTokens)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    @Bean
    public BacklogAgent backlogAgent(ChatModel model, ObjectProvider<List<AgentTool>> toolsProvider) {

        List<AgentTool> tools = toolsProvider.getIfAvailable(List::of);

        System.out.println("=== Agent tools loaded: " + tools.size() + " ===");
        tools.forEach(t -> System.out.println(" - " + t.getClass().getName()));

        var builder = AiServices.builder(BacklogAgent.class)
                .chatModel(model);

        if (!tools.isEmpty()) {
            builder.tools(tools.toArray());
        }

        return builder.build();
    }
}