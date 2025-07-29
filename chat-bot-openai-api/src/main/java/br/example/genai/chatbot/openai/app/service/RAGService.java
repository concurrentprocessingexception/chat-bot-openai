package br.example.genai.chatbot.openai.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RAGService {

    @Value("${app.vector-store.chunkSize}")
    private int chunkSize;

    @Autowired
    private VectorStore vectorStore;

    public void storeDocumentsInVectorDatabase(String document){
        log.info("Storing documents in vector database...");
        try{
            TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(chunkSize).build();
            List<Document> documents = splitter.split(Document.builder().text(document).build());
            log.debug("Created {} Vector DB documents from input", documents.size());

            log.debug("Inserting into DB...");
            vectorStore.add(documents);
            log.debug("Inserted!!!");

            log.info("Documents Stored successfully!!!");
        } catch(Exception e) {
            log.error("Exception while storing documents: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unable to store documents");
        }
    }

}

