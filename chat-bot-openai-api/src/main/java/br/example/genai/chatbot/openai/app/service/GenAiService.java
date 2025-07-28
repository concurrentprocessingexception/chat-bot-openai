package br.example.genai.chatbot.openai.app.service;

import br.example.genai.chatbot.openai.app.constants.AppConstants;
import br.example.genai.chatbot.openai.app.exception.AppException;
import br.example.genai.chatbot.openai.app.model.ChatRequest;
import br.example.genai.chatbot.openai.app.model.ChatResponse;
import br.example.genai.chatbot.openai.app.tool.ShipmentLookupTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@Slf4j
public class GenAiService {

    @Value("classpath:/prompt-template.st")
    protected Resource systemPrompt;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ShipmentLookupTool shipmentLookupTool;

    public ChatResponse handleRequest(ChatRequest chatRequest) {
        log.info("ChatService::sendMessage START");
        try {
            Prompt prompt = createPromptForChat(chatRequest);

            org.springframework.ai.chat.model.ChatResponse chatResponse = chatClient.prompt(prompt)
                    .tools(shipmentLookupTool)
                    .call()
                    .chatResponse();

            log.debug("Chat Response : {}", chatResponse);

            return parseAiResponse(chatResponse);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());
            throw new AppException(e.getMessage(), e);
        } finally {
            log.info("ChatService::sendMessage END");
        }
    }

    private ChatResponse parseAiResponse(org.springframework.ai.chat.model.ChatResponse chatResponse) {

        String responseStr = null;
        if (null != chatResponse) {
            List<Generation> generations = chatResponse.getResults();
            log.debug("Number of generation from chat client : {}", generations.size());
            responseStr = generations.get(0).getOutput().getText();
        }
        return new ChatResponse(responseStr);
    }

    private Prompt createPromptForChat(ChatRequest chatRequest) {
        log.debug("Creating prompt...");

        List<Message> messages = chatRequest.getHistory().stream().map(m -> {
            if(AppConstants.AI.equals(m.getFrom())) {
                return new AssistantMessage(m.getMessage());
            } else {
                return new UserMessage(m.getMessage());
            }
        }).collect(Collectors.toList());

        // add current message
        UserMessage userMessage = new UserMessage(chatRequest.getMessage());

        // add system message
        Message systemMessage = createSystemMessage(messages);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        log.debug("Prompt created!!! : {}", prompt);
        return prompt;
    }

    private Message createSystemMessage(List<Message> historyMessages) {

        log.debug("Creating system message...");
        try {
            String history = historyMessages.stream()
                    .map(m -> m.getMessageType().name().toLowerCase() + ": " + m.getText())
                    .collect(Collectors.joining(System.lineSeparator()));

            return new SystemPromptTemplate(this.systemPrompt)
                    .createMessage(Map.of("history", history));
        } finally {
            log.debug("System message created!!!");
        }
    }

}

