package br.com.charles.superapp.enums;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Tipos possíveis de conta bancária")
public enum TipoConta {
    CORRENTE,
    POUPANCA,
    SALARIO,
    INVESTIMENTO
}
