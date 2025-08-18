package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.dto.SimulacaoResponseDto;
import gov.caixa.simuladorservice.service.SimulacaoService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Path("/api/v1/simulacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Endpoint para simulação de empréstimos")
public class SimulacaoResource {

    private static final Logger log = LoggerFactory.getLogger(SimulacaoResource.class);

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    MeterRegistry registry;
    
    private final Timer simulacaoTimer;
    private final Counter simulacaoCounter;
    private final Counter simulacaoErroCounter;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                .build());
    }

    
    @PostConstruct
    void initMetrics() {
        simulacaoTimer = Timer.builder("simulacao.tempo_resposta")
            .description("Tempo de resposta da simulação")
            .register(registry);

        simulacaoCounter = Counter.builder("simulacao.total")
            .description("Total de simulações realizadas")
            .register(registry);

        simulacaoErroCounter = Counter.builder("simulacao.erros")
            .description("Total de erros na simulação")
            .register(registry);
    }


    @POST
    @Operation(summary = "Simula empréstimo com SAC e PRICE")
    @APIResponse(
            responseCode = "200",
            description = "Simulação realizada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Exemplo de simulação",
                            value = "{\n" +
                                    "  \"valorParcela\": 875.00,\n" +
                                    "  \"valorTotal\": 10500.00,\n" +
                                    "  \"tipo\": \"PRICE\"\n" +
                                    "}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Requisição com formato inválido",
            content = @Content(
                    mediaType = "application/problem+json",
                    examples = @ExampleObject(
                            name = "Exemplo de erro 400",
                            value = "{\n" +
                                    "  \"type\": \"https://pix.bcb.gov.br/api/v2/error/SimulacaoInvalida\",\n" +
                                    "  \"title\": \"Operação inválida.\",\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"detail\": \"O objeto simulacao.valorDesejado não respeita o schema.\",\n" +
                                    "  \"violacoes\": [\n" +
                                    "    {\n" +
                                    "      \"razao\": \"Valor menor que R$100\",\n" +
                                    "      \"propriedade\": \"valorDesejado\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"
                    )
            )
    )
    @APIResponse(
            responseCode = "401",
            description = "Usuário não autenticado",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Exemplo 401",
                            value = "{ \"error\": \"Token inválido ou ausente\" }"
                    )
            )
    )
    @APIResponse(
            responseCode = "403",
            description = "Acesso não permitido",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Exemplo 403",
                            value = "{ \"error\": \"Usuário não tem permissão para essa ação\" }"
                    )
            )
    )
    @APIResponse(
            responseCode = "429",
            description = "Limite de requisições excedido",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Exemplo 429",
                            value = "{ \"error\": \"Limite de requisições atingido. Tente novamente mais tarde.\" }"
                    )
            )
    )
    @Authenticated
    public Response simular(@Valid SimulacaoRequestDto request,
                            @Context UriInfo uriInfo,
                            @Context SecurityContext securityContext,
                            @Context HttpHeaders headers) {

        // IP do cliente (primeiro verifica X-Forwarded-For)
        String ip = headers.getHeaderString("X-Forwarded-For");
        if (ip == null) {
            ip = uriInfo.getRequestUri().getHost(); // fallback
        }
        String usuario = securityContext.getUserPrincipal().getName();

        // Rate limit
        Bucket bucket = resolveBucket(ip);
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit excedido para IP={} usuário={}", ip, usuario);
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("Limite de requisições excedido. Tente novamente mais tarde.")
                    .build();
        }

        log.info("Simulação solicitada por usuário={} IP={} valor={} prazo={}",
                usuario, ip, request.getValorDesejado(), request.getPrazo());

        
    return simulacaoTimer.record(() -> {
        simulacaoCounter.increment();

            try {
                SimulacaoResponseDto resposta = simulacaoService.simular(request);
                log.info("Simulação concluída com sucesso para usuário={}", usuario);
                return Response.ok(resposta).build();
            } catch (Exception e) {
                log.error("Erro na simulação para usuário={} IP={}", usuario, ip, e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erro interno ao processar a simulação").build();
            }
        }
    }
}
