package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SimulacaoProduto", description = "Dados da simulação por produto")
public class SimulacaoProdutoDto {
    @Schema(description = "Código do produto", example = "1")
    private Integer codigoProduto;

    @Schema(description = "Descrição do produto", example = "Produto 1")
    private String descricaoProduto;

    @Schema(description = "Taxa média de juros", example = "0.189")
    private BigDecimal taxaMediaJuro;

    @Schema(description = "Valor médio da prestação", example = "300.00")
    private BigDecimal valorMedioPrestacao;

    @Schema(description = "Valor total desejado", example = "12047.47")
    private BigDecimal valorTotalDesejado;

    @Schema(description = "Valor total de crédito", example = "16750.00")
    private BigDecimal valorTotalCredito;
}