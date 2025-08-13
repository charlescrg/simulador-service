package gov.caixa.simuladorservice.service.session;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "Sessão de chat contendo o estado atual e dados temporários da conversa")
public class ChatSession {

    @Schema(description = "Identificador único da sessão", example = "abc123")
    public String sessionId;

    @Schema(description = "Etapa atual do fluxo de conversa", example = "confirmar_pagamento")
    public String currentStep;

    @Schema(description = "Mapa de dados capturados durante a conversa (ex: código de barras, valor)")
    public Map<String, String> data = new HashMap<>();
}