package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "ListaSimulacoesResponse", description = "Resposta da listagem de simulações")
public class ListaSimulacoesResponseDto {

    @Schema(description = "Número da página", example = "1")
    private Integer pagina;

    @Schema(description = "Quantidade total de registros", example = "404")
    private Integer qtdRegistros;

    @Schema(description = "Quantidade de registros por página", example = "200")
    private Integer qtdRegistrosPagina;

    @Schema(description = "Lista de simulações")
    private List<SimulacaoResumoDto> registros;

}
