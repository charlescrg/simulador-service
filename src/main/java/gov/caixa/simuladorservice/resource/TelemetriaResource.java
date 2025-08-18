package gov.caixa.simuladorservice.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import java.util.Map;

@Path("/api/v1/telemetria")
@Produces(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    @Inject
    MeterRegistry registry;

    @GET
    public Map<String, Object> getTelemetria() {
        double total = registry.get("simulacao.total").counter().count();
        double erros = registry.get("simulacao.erros").counter().count();
        double tempoMedio = registry.get("simulacao.tempo_resposta").timer().mean(java.util.concurrent.TimeUnit.MILLISECONDS);

        return Map.of(
            "totalSimulacoes", total,
            "erros", erros,
            "tempoMedioRespostaMs", tempoMedio
        );
    }
}
