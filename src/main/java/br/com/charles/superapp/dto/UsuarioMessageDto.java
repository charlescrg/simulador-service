package br.com.charles.superapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mensagem enviada pelo usuário no chat")
public class UsuarioMessageDto {

    @Schema(description = "Identificador único da sessão do chat", example = "sessao123")
    private String sessionId;

    @Schema(description = "Mensagem enviada pelo usuário", example = "Sim")
    private String message;
}
