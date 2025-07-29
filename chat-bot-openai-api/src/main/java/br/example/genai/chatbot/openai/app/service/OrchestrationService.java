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
public class OrchestrationService {

    @Value("classpath:/prompt-template-orchestration.st")
    protected Resource systemPrompt;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private GenAiService genAiService;

    @Autowired
    private QnAService qnAService;

    public ChatResponse handleRequest(ChatRequest chatRequest) {
        log.info("OrchestrationService::handleRequest START");
        try {
            Prompt prompt = createPromptForChat(chatRequest);

            org.springframework.ai.chat.model.ChatResponse chatResponse = chatClient.prompt(prompt)
                    .call()
                    .chatResponse();
            log.debug("Chat Response : {}", chatResponse);

            String intent = identifyIntent(chatResponse);
            log.debug("Intent : {}", intent);

            if(AppConstants.API.equalsIgnoreCase(intent)) {
                return genAiService.handleRequest(chatRequest);
            } else if(AppConstants.QnA.equalsIgnoreCase(intent)) {
                return qnAService.handleRequest(chatRequest);
            } else {
                return genAiService.handleRequest(chatRequest);
            }
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());
            throw new AppException(e.getMessage(), e);
        } finally {
            log.info("OrchestrationService::handleRequest END");
        }
    }

    private String identifyIntent(org.springframework.ai.chat.model.ChatResponse chatResponse) {
        if(null != chatResponse) {
            List<Generation> generations = chatResponse.getResults();
            log.debug("Number of generations from chat client : {}", generations.size());
            return generations.get(0).getOutput().getText();
        }
        return "";
    }

    private Prompt createPromptForChat(ChatRequest chatRequest) {
        log.debug("Creating prompt...");

        // add system message
        Message systemMessage = new SystemPromptTemplate(this.systemPrompt)
                .createMessage(Map.of("query", chatRequest.getMessage()));;

        // add current message
        UserMessage userMessage = new UserMessage(chatRequest.getMessage());

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        log.debug("Prompt created!!! : {}", prompt);
        return prompt;
    }
}

