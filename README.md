# Chat BOT Open AI

A simple Chat Bot application with a basic UI, interacting with Open AI API.

It consists of two modules

* chat-bot-openai-api
* chat-bot-openai-external-api
* chat-bot-openai-ui

## chat-bot-openai-api

A spring boot REST api, which exposes a single endpoint which is called from the UI application.

You need to replace below configuration in application.yml for the application to work. 
* NEO4J_DB_URL
* NEO4J_DB_USERNAME
* NEO4J_DB_PASSWORD
* OPENAI_API_KEY

### How to run?
**This will require a docker runtime on your machine.**
1. go to project root directory on the command line and run the following command
    ```
    docker build -t chat-bot-openai-api .
    ```
2. Run the folowing command. 
    ```
    docker run -p 8080:8080 chat-bot-openai-api
    ```
## chat-bot-openai-external-api

A spring boot REST api, which exposes a single endpoint. This api will act as a external api which will provides dummy shipment information. This api will be used by chatbot in the function calling use-case.
### How to run?
**This will require a docker runtime on your machine.**
1. go to project root directory on the command line and run the following command
    ```
    docker build -t chat-bot-openai-external-api .
    ```
2. Run the folowing command. 
    ```
    docker run -p 8081:8081 chat-bot-openai-external-api
    ```
## chat-bot-openai-ui

A react SPA with a chatbox window which interacts with chat-bot-open-api REST service.
### How to run?
**This will require node installed on your machine**
```
npm start
```
