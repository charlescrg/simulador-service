package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.dto.BotReplyDto;
import gov.caixa.simuladorservice.dto.UsuarioMessageDto;
import gov.caixa.simuladorservice.service.session.ChatSession;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/api/chat")
@Tag(name = "Chat", description = "Fluxo de conversação do assistente virtual")
@RolesAllowed("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {

    private static final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    @POST
    @Operation(summary = "Processa uma mensagem do usuário",
            description = "Recebe a mensagem e mantém o estado da conversa usando uma sessão de chat.")
    @APIResponse(responseCode = "200", description = "Resposta do assistente virtual",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = BotReplyDto.class)))
    public Response chat(UsuarioMessageDto usuarioMessage) {
        String sessionId = usuarioMessage.getSessionId();
        ChatSession session = sessions.computeIfAbsent(sessionId, id -> new ChatSession());

        String usuarioInput = usuarioMessage.getMessage();
        String reply;

        if (session.currentStep == null) {
            reply = "Você quer pagar uma conta?";
            session.currentStep = "confirmar_pagamento";
        } else if ("confirmar_pagamento".equals(session.currentStep)) {
            if (usuarioInput.toLowerCase().contains("sim")) {
                reply = "Digite o código de barras.";
                session.currentStep = "codigo_barras";
            } else {
                reply = "Ok, operação cancelada.";
                sessions.remove(sessionId);
            }
        } else if ("codigo_barras".equals(session.currentStep)) {
            session.data.put("codigo_barras", usuarioInput);
            reply = "Qual o valor da conta?";
            session.currentStep = "valor";
        } else if ("valor".equals(session.currentStep)) {
            session.data.put("valor", usuarioInput);
            reply = String.format("Confirma o pagamento de R$ %s para o código %s?",
                    session.data.get("valor"), session.data.get("codigo_barras"));
            session.currentStep = "confirmacao_final";
        } else if ("confirmacao_final".equals(session.currentStep)) {
            if (usuarioInput.toLowerCase().contains("sim")) {
                // Aqui você pode integrar com serviço real de pagamento
                reply = "Pagamento realizado com sucesso!";
            } else {
                reply = "Pagamento cancelado.";
            }
            sessions.remove(sessionId);
        } else {
            reply = "Desculpe, não entendi. Pode repetir?";
        }

        return Response.ok(new BotReplyDto(reply)).build();
    }
}
