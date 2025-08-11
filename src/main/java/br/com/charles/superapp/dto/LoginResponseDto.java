package br.com.charles.superapp.dto;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@Schema(name = "LoginResponseDto", description = "Resposta com o token JWT após autenticação bem-sucedida")
public class LoginResponseDto {

    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    public LoginResponseDto(String token) {
        this.token = token;
    }
}

