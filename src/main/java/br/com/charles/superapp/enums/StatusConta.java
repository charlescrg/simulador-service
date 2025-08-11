package br.com.charles.superapp.enums;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Status da conta bancária")
public enum StatusConta {
    ATIVA,
    BLOQUEADA,
    ENCERRADA
}