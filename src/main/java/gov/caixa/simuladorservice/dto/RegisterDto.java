package gov.caixa.simuladorservice.dto;

import gov.caixa.simuladorservice.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@Schema(name = "RegisterDto", description = "Dados para cadastro de novo usuário")
public class RegisterDto {

    @Schema(description = "Login de acesso único", example = "carlos.souza")
    @NotBlank(message = "Login é obrigatório")
    private String login;

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    @Schema(description = "Nome completo do usuário", example = "Carlos Souza")
    private String nomeCompleto;

    @Schema(description = "E-mail válido do usuário", example = "carlos@empresa.com")
    @Email(message = "E-mail inválido")
    private String email;

    @Schema(description = "Perfil do usuário no sistema")
    private UserRole role;
}
