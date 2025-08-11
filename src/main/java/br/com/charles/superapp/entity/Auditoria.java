package br.com.charles.superapp.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Registros de auditoria do sistema")
public class Auditoria extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do registro de auditoria", example = "1")
    private Long id;

    @Schema(description = "Ação executada", example = "LOGIN / LOGOUT / TRANSFERÊNCIA")
    private String acao;

    @Schema(description = "Data e hora da ação")
    private LocalDateTime dataHora;

    @Schema(description = "Endereço IP do usuário")
    private String ip;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @Schema(description = "Usuário que executou a ação")
    private Usuario usuario;
}
