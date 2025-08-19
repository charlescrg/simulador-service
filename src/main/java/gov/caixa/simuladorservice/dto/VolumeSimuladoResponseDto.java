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
@Schema(name = "VolumeSimuladoResponse", description = "Volume simulado por produto e por dia")
public class VolumeSimuladoResponseDto {

    @Schema(description = "Data de referência", example = "2025-07-30")
    private LocalDate dataReferencia;

    @Schema(description = "Lista de simulações por produto")
    private List<SimulacaoProdutoDto> simulacoes;
}
