package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SimulacaoResponse", description = "Resposta da simulação do empréstimo")
public class SimulacaoResponseDto {

    @Schema(description = "Código do produto", example = "1")
    private Long codigoProduto;

    @Schema(description = "Descrição do produto", example = "Empréstimo Pessoal")
    private String descricaoProduto;

    @Schema(description = "Taxa de juros aplicada", example = "0.05")
    private BigDecimal taxaJuros;

    @Schema(description = "Lista de resultados de simulação (SAC e PRICE)")
    private List<ResultadoSimulacaoDto> resultadoSimulacao;
}
