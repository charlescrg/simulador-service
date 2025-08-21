package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ErroValidacao", description = "Erro de validação retornado pela API")
public class ErroValidacaoDto {

    @Schema(description = "Tipo do erro", example = "https://pix.bcb.gov.br/api/v2/error/SimulacaoInvalida")
    private String type;

    @Schema(description = "Título do erro", example = "Operação inválida.")
    private String title;

    @Schema(description = "Código HTTP do erro", example = "400")
    private int status;

    @Schema(description = "Detalhes do erro", example = "O objeto simulacao.valorDesejado não respeita o schema.")
    private String detail;

    @Schema(description = "Lista de violações específicas")
    private List<ViolacaoDto> violacoes;
}
