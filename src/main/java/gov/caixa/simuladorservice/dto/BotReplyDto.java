package gov.caixa.simuladorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta enviada pelo bot do chat")
public class BotReplyDto {

    @Schema(description = "Mensagem de resposta do bot", example = "Pagamento realizado com sucesso!")
    private String reply;
}
