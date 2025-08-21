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
@Schema(name = "ErroPadrao", description = "Erro genérico retornado pela API")
public class ErroPadraoDto {

    @Schema(description = "Mensagem de erro", example = "Token inválido ou ausente")
    private String error;
}
