package br.example.genai.chatbot.openai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "br.example.genai.chatbot.openai.app")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
