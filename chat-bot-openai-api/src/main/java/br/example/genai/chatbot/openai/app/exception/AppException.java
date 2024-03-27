package br.example.genai.chatbot.openai.app.exception;

/**
 * Global Exception class for the application.
 */
public class AppException extends RuntimeException {

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
