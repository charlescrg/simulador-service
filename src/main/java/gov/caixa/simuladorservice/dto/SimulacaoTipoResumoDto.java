package gov.caixa.simuladorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Resumo de um tipo de simulação (SAC ou PRICE)")
public class SimulacaoTipoResumoDto {

    @Schema(description = "Tipo de simulação", example = "SAC")
    private String tipo;

    @Schema(description = "Prazo da simulação em meses", example = "5")
    private int prazo;

    @Schema(description = "Valor total das parcelas para este tipo de simulação", example = "1243.28")
    private BigDecimal valorTotalParcelas;
}


