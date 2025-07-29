package br.example.genai.chatbot.openai.app.controller;

import br.example.genai.chatbot.openai.app.model.ChatRequest;
import br.example.genai.chatbot.openai.app.model.ChatResponse;
import br.example.genai.chatbot.openai.app.service.GenAiService;
import br.example.genai.chatbot.openai.app.service.OrchestrationService;
import br.example.genai.chatbot.openai.app.service.RAGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
public class AppController {

    @Autowired
    private GenAiService genAiService;

    @Autowired
    private RAGService ragService;

    @Autowired
    private OrchestrationService orchestrationService;

    @PostMapping("/app/chat")
    public ResponseEntity<ChatResponse> handleRequest(@RequestBody ChatRequest chatRequest) {
        log.info("Request Received : {}", chatRequest);
        ChatResponse chatResponse = null;
        try {
            chatResponse = orchestrationService.handleRequest(chatRequest);
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e ) {
            log.error("Error occurred while processing request : {}", e.getMessage());
            chatResponse = new ChatResponse("Error!!! Please try again!!!");
            return ResponseEntity.ok(chatResponse);
        } finally {
            log.info("Request processing Complete!!! Response : {}", chatResponse);
        }
    }

    @PostMapping(value = "/app/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        log.info("Received file: {}", file.getOriginalFilename());

        try {
            String documentText = new String(file.getBytes());
            log.debug("Document to be stored : {}", documentText);
            ragService.storeDocumentsInVectorDatabase(documentText);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        } catch (Exception e ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }
}
