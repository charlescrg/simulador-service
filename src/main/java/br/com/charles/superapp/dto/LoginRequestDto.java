package br.com.charles.superapp.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "LoginRequestDto", description = "Dados para autenticação do usuário")
public class LoginRequestDto {

    @Schema(description = "Login do usuário", example = "usuario123", required = true)
    public String login;

    @Schema(description = "Senha do usuário", example = "senhaSecreta", required = true)
    public String password;
}

