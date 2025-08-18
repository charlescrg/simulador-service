
package gov.caixa.simuladorservice.resource;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Path("/api/v1/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Telemetria", description = "Endpoint para consulta de métricas dos serviços")
public class TelemetriaResource {

    @Inject
    MeterRegistry registry;

    private Timer simulacaoTimer;
    private Counter simulacaoCounter;
    private Counter simulacaoErroCounter;

    private Timer listarSimulacoesTimer;
    private Counter listarSimulacoesCounter;
    private Counter listarSimulacoesErroCounter;

    private Timer volumeSimuladoTimer;
    private Counter volumeSimuladoCounter;
    private Counter volumeSimuladoErroCounter;

    private Timer telemetriaConsultaTimer;
    private Counter telemetriaConsultaCounter;
    private Counter telemetriaConsultaErroCounter;

    @PostConstruct
    void initMetrics() {
        simulacaoTimer = registry.find("simulacao.tempo_resposta").timer();
        simulacaoCounter = registry.find("simulacao.total").counter();
        simulacaoErroCounter = registry.find("simulacao.erros").counter();

        listarSimulacoesTimer = registry.find("listar_simulacoes.tempo_resposta").timer();
        listarSimulacoesCounter = registry.find("listar_simulacoes.total").counter();
        listarSimulacoesErroCounter = registry.find("listar_simulacoes.erros").counter();

        volumeSimuladoTimer = registry.find("volume_simulado.tempo_resposta").timer();
        volumeSimuladoCounter = registry.find("volume_simulado.total").counter();
        volumeSimuladoErroCounter = registry.find("volume_simulado.erros").counter();

        telemetriaConsultaTimer = registry.find("telemetria_consulta.tempo_resposta").timer();
        telemetriaConsultaCounter = registry.find("telemetria_consulta.total").counter();
        telemetriaConsultaErroCounter = registry.find("telemetria_consulta.erros").counter();
    }

    @GET
    @Operation(summary = "Consulta dados de telemetria dos serviços")
    @APIResponse(
        responseCode = "200",
        description = "Dados de telemetria agregados por serviço",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
    )
    public Map<String, Object> getTelemetria() {
        return Map.of(
            "simulacao", Map.of(
                "total", simulacaoCounter != null ? simulacaoCounter.count() : 0,
                "erros", simulacaoErroCounter != null ? simulacaoErroCounter.count() : 0,
                "tempoMedioMs", simulacaoTimer != null ? simulacaoTimer.mean(TimeUnit.MILLISECONDS) : 0
            ),
            "listarSimulacoes", Map.of(
                "total", listarSimulacoesCounter != null ? listarSimulacoesCounter.count() : 0,
                "erros", listarSimulacoesErroCounter != null ? listarSimulacoesErroCounter.count() : 0,
                "tempoMedioMs", listarSimulacoesTimer != null ? listarSimulacoesTimer.mean(TimeUnit.MILLISECONDS) : 0
            ),
            "volumeSimulado", Map.of(
                "total", volumeSimuladoCounter != null ? volumeSimuladoCounter.count() : 0,
                "erros", volumeSimuladoErroCounter != null ? volumeSimuladoErroCounter.count() : 0,
                "tempoMedioMs", volumeSimuladoTimer != null ? volumeSimuladoTimer.mean(TimeUnit.MILLISECONDS) : 0
            ),
            "telemetriaConsulta", Map.of(
                "total", telemetriaConsultaCounter != null ? telemetriaConsultaCounter.count() : 0,
                "erros", telemetriaConsultaErroCounter != null ? telemetriaConsultaErroCounter.count() : 0,
                "tempoMedioMs", telemetriaConsultaTimer != null ? telemetriaConsultaTimer.mean(TimeUnit.MILLISECONDS) : 0
            )
        );
    }
}
