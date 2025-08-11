package br.com.charles.superapp.entity;

import br.com.charles.superapp.enums.TipoPerfilUsuario;
import br.com.charles.superapp.enums.UserRole;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Usuários do sistema para autenticação e personalização do app")
public class Usuario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do usuário", example = "1")
    private Long id;

    @Schema(description = "Login de acesso", example = "carlos.souza")
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Schema(description = "Senha criptografada")
    @Column(name = "senha", nullable = false)
    private String senha;

    @Schema(description = "Nome completo do usuário", example = "Carlos Souza")
    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Schema(description = "E-mail do usuário", example = "carlos@empresa.com")
    @Column(name = "email")
    private String email;

    @Schema(description = "Telefone do usuário", example = "+55 62 99999-0000")
    @Column(name = "telefone")
    private String telefone;

    @Schema(description = "Data de nascimento do usuário")
    @Column(name = "data_nascimento")
    private String dataNascimento;

    @Schema(description = "Tipo de perfil do usuário para personalização do app")
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil")
    private TipoPerfilUsuario perfil;

    @Schema(description = "Renda mensal do usuário", example = "3500.00")
    @Column(name = "renda_mensal", precision = 12, scale = 2)
    private BigDecimal rendaMensal;

    @Schema(description = "Preferência de idioma", example = "pt-BR")
    @Column(name = "idioma")
    private String idioma;

    @Schema(description = "Tema escolhido no app", example = "DARK")
    @Column(name = "tema")
    private String tema;

    @Schema(description = "Se o usuário aceita receber notificações push")
    @Column(name = "notificacoes_ativas",nullable = false)
    private Boolean notificacoesAtivas;

    @Schema(description = "Data do último login")
    @Column(name = "ultimo_login")
    private String ultimoLogin;

    @Schema(description = "Conta está ativa?")
    @Column(name = "ativo",nullable = false)
    private Boolean ativo;

    @Schema(description = "Regra de segurança")
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

}
