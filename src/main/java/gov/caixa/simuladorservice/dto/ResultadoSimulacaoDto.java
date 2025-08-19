package gov.caixa.simuladorservice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@Builder
@Schema(name = "ResultadoSimulacao", description = "Resultado de uma simulação (SAC ou PRICE)")
@JsonPropertyOrder({ "tipo", "parcelas" })
public class ResultadoSimulacaoDto {

    @Schema(description = "Tipo de simulação", example = "SAC")
    private String tipo;

    @Schema(description = "Lista de parcelas calculadas")
    private List<ParcelaDto> parcelas;
}
