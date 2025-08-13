package gov.caixa.simuladorservice.dto;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Schema(name = "SimulacaoRequest", description = "Dados da requisição de simulação")
public class SimulacaoRequestDto {

    @Schema(description = "Valor desejado para o empréstimo", example = "10000")
    private BigDecimal valorDesejado;

    @Schema(description = "Prazo em meses para pagamento", example = "24")
    private Integer prazo;
}
