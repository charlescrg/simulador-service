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

    @Schema(description = "Nome da API/endpoint", example = "Simulacao")
    private String nomeApi;

    @Schema(description = "Quantidade de requisições", example = "135")
    private long qtdRequisicoes;

    @Schema(description = "Tempo médio de resposta em milissegundos", example = "150")
    private double tempoMedio;

    @Schema(description = "Tempo mínimo de resposta em milissegundos", example = "23")
    private long tempoMinimo;

    @Schema(description = "Tempo máximo de resposta em milissegundos", example = "860")
    private long tempoMaximo;

    @Schema(description = "Percentual de requisições com sucesso", example = "0.98")
    private double percentualSucesso;
}