package br.com.charles.superapp.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import lombok.*;

@Entity
@Table(name = "agencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informações sobre a agência bancária")
public class Agencia extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da agência", example = "1")
    private Long id;

    @Schema(description = "Número da agência", example = "1234")
    private String numero;

    @Schema(description = "Nome da agência", example = "Agência Centro Goiânia")
    private String nome;

    @Schema(description = "Endereço completo", example = "Av. Goiás, 1000, Goiânia-GO")
    private String endereco;

    @Schema(description = "Telefone de contato", example = "(62) 3333-4444")
    private String telefone;

    @Schema(description = "Gerente responsável", example = "Maria Souza")
    private String gerente;
}
