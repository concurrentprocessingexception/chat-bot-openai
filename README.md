# Chat BOT Open AI

A simple Chat Bot application with a basic UI, interacting with Open AI API.

It consists of two modules

- chat-bot-openai-api
- chat-bot-openai-ui

## chat-bot-openai-api

A spring boot REST api, which exposes a single endpoint which is called from the UI application.

You need to replace YOUR_API_KEY in application.yml file with your own Open AI API KEY.

## chat-bot-openai-ui

A react SPA with a chatbox window which interacts with chat-bot-open-api REST service.
