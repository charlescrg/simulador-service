package br.com.charles.superapp.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informações do cliente do banco")
public class Cliente extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do cliente", example = "1")
    private Long id;

    @Schema(description = "Nome completo do cliente", example = "João da Silva")
    private String nome;

    @Schema(description = "CPF ou CNPJ do cliente", example = "123.456.789-00")
    @Column(unique = true, nullable = false)
    private String documento;

    @Schema(description = "Data de nascimento (formato yyyy-MM-dd)", example = "1985-06-15")
    private String dataNascimento;

    @Schema(description = "Endereço completo do cliente", example = "Rua das Flores, 123, Goiânia-GO")
    private String endereco;

    @Schema(description = "Telefone de contato", example = "(62) 99999-9999")
    private String telefone;

    @Schema(description = "Email de contato", example = "joao@email.com")
    private String email;

    @Schema(description = "Situação do cliente", example = "Ativo")
    private String situacao;
}

