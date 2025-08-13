package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.dto.SimulacaoResponseDto;
import gov.caixa.simuladorservice.service.SimulacaoService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/api/simulacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Endpoint para simulação de empréstimosulacaoResource {

    private static final Logger log = LoggerFactory.getLogger(SimulacaoResource.class);

    @Inject
    SimulacaoService simulacaoService;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
            .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
            .build());
    }

    @POST
    @Operation(summary = "Simula empréstimo com SAC e PRICE")
    @Authenticated
    public Response simular(@Valid SimulacaoRequestDto request,
                            @Context HttpServletRequest httpRequest,
                            @Context SecurityContext securityContext) {

        String ip = httpRequest.getRemoteAddr();
        String usuario = securityContext.getUserPrincipal().getName();

        Bucket bucket = resolveBucket(ip);
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit excedido para IP={} usuário={}", ip, usuario);
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity("Limite de requisições excedido. Tente novamente mais tarde.")
                .build();
        }

        log.info("Simulação solicitada por usuário={} IP={} valor={} prazo={}",
                 usuario, ip, request.getValorDesejado(), request.getPrazo());

        try {
            SimulacaoResponseDto resposta = simulacaoService.simular(request);
            log.info("Simulação concluída com sucesso para usuário={}", usuario);
            return Response.ok(resposta).build();
        } catch (Exception e) {
            log.error("Erro na simulação para usuário={} IP={}", usuario, ip, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
