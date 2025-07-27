package br.example.genai.chatbot.openai.app.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(OpenAiChatModel.builder()
                        .openAiApi(OpenAiApi.builder()
                                .apiKey(apiKey)
                                .build())
                        .build())
                .build();
    }
}
