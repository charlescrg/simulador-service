package gov.caixa.simuladorservice.entity.simulacao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TELEMETRIA")
@Schema(name = "Telemetria", description = "Métricas de desempenho das APIs do simulador")
public class TelemetriaEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da telemetria", example = "1")
    private Long id;

    @Schema(description = "Nome da API monitorada", example = "simular")
    private String nomeApi;

    @Schema(description = "Quantidade total de requisições registradas", example = "150")
    private Long qtdRequisicoes;

    @Schema(description = "Tempo médio de resposta em ms", example = "120.5")
    private Double tempoMedio;

    @Schema(description = "Tempo mínimo registrado em ms", example = "80")
    private Long tempoMinimo;

    @Schema(description = "Tempo máximo registrado em ms", example = "300")
    private Long tempoMaximo;

    @Schema(description = "Percentual de sucesso das requisições (%)", example = "98.5")
    private Double percentualSucesso;

    @Schema(description = "Data da coleta das métricas", example = "2025-08-24")
    private LocalDate data;
}
