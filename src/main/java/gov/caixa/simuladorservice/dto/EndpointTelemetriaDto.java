package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "EndpointTelemetria", description = "Dados de telemetria de um endpoint")
public class EndpointTelemetriaDto {
    @Schema(description = "Nome da API", example = "Simulacao")
    private String nomeApi;

    @Schema(description = "Quantidade de requisições", example = "135")
    private Integer qtdRequisicoes;

    @Schema(description = "Tempo médio de resposta em milissegundos", example = "150")
    private Integer tempoMedio;

    @Schema(description = "Tempo mínimo de resposta em milissegundos", example = "23")
    private Integer tempoMinimo;

    @Schema(description = "Tempo máximo de resposta em milissegundos", example = "860")
    private Integer tempoMaximo;

    @Schema(description = "Percentual de sucesso", example = "0.98")
    private Double percentualSucesso;
}