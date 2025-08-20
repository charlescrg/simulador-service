package gov.caixa.simuladorservice.telemetria;

import gov.caixa.simuladorservice.dto.EndpointTelemetriaDto;
import gov.caixa.simuladorservice.dto.TelemetriaResponseDto;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@Priority(1)
public class TelemetriaInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    MetricRegistry registry;

    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Counter> successCounters = new ConcurrentHashMap<>();

    private static final String START_TIME = "start-time";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(START_TIME, System.nanoTime());

        String path = requestContext.getUriInfo().getPath();
        timers.computeIfAbsent(path, p -> registry.timer(p + ".tempo_resposta"));
        counters.computeIfAbsent(path, p -> registry.counter(p + ".total"));
        successCounters.computeIfAbsent(path, p -> registry.counter(p + ".sucesso"));
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        long start = (long) requestContext.getProperty(START_TIME);
        long durationNanos = System.nanoTime() - start;

        String path = requestContext.getUriInfo().getPath();

        Timer timer = timers.get(path);
        Counter total = counters.get(path);
        Counter success = successCounters.get(path);

        if (timer != null) {
            timer.update(Duration.ofNanos(durationNanos));
        }
        if (total != null) total.inc();
        if (success != null && responseContext.getStatus() >= 200 && responseContext.getStatus() < 300) {
            success.inc();
        }
    }

    public TelemetriaResponseDto gerarRelatorio() {
        LocalDate hoje = LocalDate.now();
        List<EndpointTelemetriaDto> lista = new ArrayList<>();

        for (String path : timers.keySet()) {
            String nomeApi = mapPathToNomeApi(path);
            Timer timer = timers.get(path);
            Counter total = counters.get(path);
            Counter success = successCounters.get(path);

            long qtdRequisicoes = total != null ? total.getCount() : 0;
            double tempoMedio = 0;
            long tempoMinimo = 0;
            long tempoMaximo = 0;

            if (timer != null && qtdRequisicoes > 0) {
                tempoMedio = Math.round(timer.getSnapshot().getMean() / 1_000_000.0 * 100.0) / 100.0;
                tempoMinimo = timer.getSnapshot().getMin() / 1_000_000;
                tempoMaximo = timer.getSnapshot().getMax() / 1_000_000;
            }

            double percentualSucesso = (qtdRequisicoes > 0 && success != null)
                    ? Math.round(((double) success.getCount() / qtdRequisicoes) * 100.0) / 100.0
                    : 0;

            lista.add(new EndpointTelemetriaDto(
                    nomeApi,
                    qtdRequisicoes,
                    tempoMedio,
                    tempoMinimo,
                    tempoMaximo,
                    percentualSucesso
            ));
        }

        return new TelemetriaResponseDto(hoje, lista);
    }
    private String mapPathToNomeApi(String path) {
        return switch (path) {
            case "/api/v1/simulacoes" -> "Simulacao";
            case "/api/v1/simulacoes/listar" -> "ListarSimulacoes";
            case "/api/v1/simulacoes/valores-por-produto-dia" -> "VolumeSimulado";
            case "/api/v1/telemetria" -> "Telemetria";
            default -> path;
        };
    }
}
