package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "TelemetriaResponse", description = "Dados de telemetria por endpoint")
public class TelemetriaResponseDto {

    @Schema(description = "Data de referência", example = "2025-07-30")
    private LocalDate dataReferencia;

    @Schema(description = "Lista de endpoints monitorados")
    private List<EndpointTelemetriaDto> listaEndpoints;
}
