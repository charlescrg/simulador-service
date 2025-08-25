package gov.caixa.simuladorservice.entity.simulacao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "TELEMETRIA")
@Schema(name = "Telemetria", description = "Métricas de desempenho das APIs do simulador")
public class TelemetriaEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Schema(description = "Identificador único da telemetria", example = "1")
    private Long id;

    @Column(name = "NOME_API")
    @Schema(description = "Nome da API monitorada", example = "simular")
    private String nomeApi;

    @Column(name = "TEMPO_MS")
    @Schema(description = "Tempo de resposta em ms", example = "120.5")
    private Long tempoMs;

    @Column(name = "SUCESSO")
    @Schema(description = "Percentual de sucesso das requisições (%)", example = "98.5")
    private Integer sucesso;

    @Column(name = "DATA_REFERENCIA")
    @Schema(description = "Data da coleta das métricas", example = "2025-08-24")
    private LocalDate data;
}
