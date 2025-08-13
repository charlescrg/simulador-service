package gov.caixa.simuladorservice.dto;

import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@Builder
@Schema(name = "ResultadoSimulacao", description = "Resultado de uma simulação (SAC ou PRICE)")
public class ResultadoSimulacaoDto {

    @Schema(description = "Tipo de simulação", example = "SAC")
    private String tipo;

    @Schema(description = "Lista de parcelas calculadas")
    private List<ParcelaDto> parcelas;
}
