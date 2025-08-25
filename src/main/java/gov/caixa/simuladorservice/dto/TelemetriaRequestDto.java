package gov.caixa.simuladorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TelemetriaRequestDto", description = "Dados enviados para registrar uma métrica de telemetria")
public class TelemetriaRequestDto {

    @Schema(description = "Nome da API monitorada", example = "/api/v1/simulacoes")
    private String nomeApi;

    @Schema(description = "Tempo de resposta em milissegundos", example = "120")
    private long tempoMs;

    @Schema(description = "Indica se a requisição foi bem-sucedida (1 = sucesso, 0 = falha)", example = "1")
    private Integer sucesso;
}
