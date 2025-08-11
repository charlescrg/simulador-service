package br.com.charles.superapp.controller;

import jakarta.ws.rs.*;

@Path("/chat")
public class ChatController {

    private static final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    @POST
    public Response chat(UserMessage userMessage) {
        String sessionId = userMessage.getSessionId();
        ChatSession session = sessions.computeIfAbsent(sessionId, id -> new ChatSession());

        String userInput = userMessage.getMessage();
        String reply;

        switch (session.currentStep) {
            case null -> {
                reply = "Você quer pagar uma conta?";
                session.currentStep = "confirmar_pagamento";
            }
            case "confirmar_pagamento" -> {
                if (userInput.toLowerCase().contains("sim")) {
                    reply = "Digite o código de barras.";
                    session.currentStep = "codigo_barras";
                } else {
                    reply = "Ok, operação cancelada.";
                    sessions.remove(sessionId);
                }
            }
            case "codigo_barras" -> {
                session.data.put("codigo_barras", userInput);
                reply = "Qual o valor da conta?";
                session.currentStep = "valor";
            }
            case "valor" -> {
                session.data.put("valor", userInput);
                reply = String.format("Confirma o pagamento de R$ %s para o código %s?",
                        session.data.get("valor"), session.data.get("codigo_barras"));
                session.currentStep = "confirmacao_final";
            }
            case "confirmacao_final" -> {
                if (userInput.toLowerCase().contains("sim")) {
                    // Aqui você pode chamar um serviço real de pagamento
                    reply = "Pagamento realizado com sucesso!";
                } else {
                    reply = "Pagamento cancelado.";
                }
                sessions.remove(sessionId);
            }
            default -> reply = "Desculpe, não entendi. Pode repetir?";
        }

        return Response.ok(new BotReply(reply)).build();
    }
}
