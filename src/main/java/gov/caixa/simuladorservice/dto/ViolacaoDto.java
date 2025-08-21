package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Violacao", description = "Detalhes de uma violação de validação")
public class ViolacaoDto {

    @Schema(description = "Razão da violação", example = "Valor menor que R$100")
    private String razao;

    @Schema(description = "Propriedade afetada", example = "valorDesejado")
    private String propriedade;
}
