package gov.caixa.simuladorservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "SIMULACAO", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidade que representa uma simulação de produto de empréstimo")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da simulação", example = "1")
    private Long id;

    @Column(name = "PRODUTO")
    @Schema(description = "Nome do produto de empréstimo", example = "Crédito Consignado")
    private String produto;

    @Column(name = "DATA_SIMULACAO")
    @Schema(description = "Data em que a simulação foi realizada", example = "2025-08-19")
    private LocalDate dataSimulacao;

    @Column(name = "VALOR_SIMULADO")
    @Schema(description = "Valor simulado para o produto", example = "15000.00")
    private BigDecimal valorSimulado;

    @Column(name = "TEMPO_RESPOSTA_MS")
    @Schema(description = "Tempo de resposta da simulação em milissegundos", example = "120")
    private Long tempoRespostaMs;

    @Column(name = "VALOR_TOTAL_CREDITO")
    @Schema(description = "Valor total de crédito calculado na simulação", example = "16000.00")
    private BigDecimal valorTotalCredito;

    @OneToMany(mappedBy = "simulacao", cascade = CascadeType.ALL)
    private List<SimulacaoTipo> tipos;
}
