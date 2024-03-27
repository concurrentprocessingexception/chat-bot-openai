package br.example.genai.chatbot.openai.app.controller;

import br.example.genai.chatbot.openai.app.model.ChatRequest;
import br.example.genai.chatbot.openai.app.model.ChatResponse;
import br.example.genai.chatbot.openai.app.service.GenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AppController {

    @Autowired
    private GenAiService genAiService;

    @PostMapping("/app/chat")
    public ResponseEntity<ChatResponse> handleRequest(@RequestBody ChatRequest chatRequest) {
        log.info("Request Received : {}", chatRequest);
        ChatResponse chatResponse = null;
        try {
            chatResponse = genAiService.handleRequest(chatRequest);
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e ) {
            log.error("Error occurred while processing request : {}", e.getMessage());
            chatResponse = new ChatResponse("Error!!! Please try again!!!");
            return ResponseEntity.ok(chatResponse);
        } finally {
            log.info("Request processing Complete!!! Response : {}", chatResponse);
        }
    }
}
