package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SimulacaoResumo", description = "Resumo da simulação")
public class SimulacaoResumoDto {
    @Schema(description = "ID da simulação", example = "20180702")
    private Integer idSimulacao;

    @Schema(description = "Valor desejado", example = "900.00")
    private BigDecimal valorDesejado;

    @Schema(description = "Prazo em meses", example = "5")
    private Integer prazo;

    @Schema(description = "Valor total das parcelas", example = "1243.28")
    private List<ValorTotalParcelasTipoDto> valorTotalParcelas;
}
