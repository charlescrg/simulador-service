package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "SimulacaoProduto", description = "Dados da simulação por produto")
public class SimulacaoProdutoDto {
    @Schema(description = "Código do produto", example = "1")
    private Integer codigoProduto;

    @Schema(description = "Descrição do produto", example = "Produto 1")
    private String descricaoProduto;

    @Schema(description = "Valor total desejado", example = "12047.47")
    private BigDecimal valorTotalDesejado;

    @Schema(description = "Taxa média de juros SAC", example = "0.018")
    private BigDecimal taxaMediaJuroSAC;

    @Schema(description = "Taxa média de juros PRICE", example = "0.018")
    private BigDecimal taxaMediaJuroPRICE;

    @Schema(description = "Valor médio da prestação SAC", example = "190.00")
    private BigDecimal valorMedioPrestacaoSAC;

    @Schema(description = "Valor médio da prestação PRICE", example = "190.00")
    private BigDecimal valorMedioPrestacaoPRICE;

    @Schema(description = "Valor total de crédito SAC", example = "950.00")
    private BigDecimal valorTotalCreditoSAC;

    @Schema(description = "Valor total de crédito PRICE", example = "950.00")
    private BigDecimal valorTotalCreditoPRICE;
}
