package br.com.charles.superapp.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Registro de movimentações financeiras")
public class Transacao extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da transação", example = "5001")
    private Long id;

    @Schema(description = "Data e hora da transação", example = "2025-08-07T10:15:30")
    private LocalDateTime dataHora;

    @Schema(description = "Tipo da transação", example = "Crédito")
    private String tipo;

    @Schema(description = "Valor da transação", example = "200.50")
    private Double valor;

    @Schema(description = "Descrição da transação", example = "Transferência para conta 12345-6")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "conta_origem_id")
    @Schema(description = "Conta de origem")
    private ContaBancaria contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id")
    @Schema(description = "Conta de destino")
    private ContaBancaria contaDestino;

    @Schema(description = "Status da transação", example = "Concluída")
    private String status;
}
