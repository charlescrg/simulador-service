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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/api/simulacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Endpoint para simulação de empréstimos")
public class SimulacaoResource {

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
    @APIResponse(responseCode = "200", description = "Simulação realizada com sucesso")
    @APIResponse(responseCode = "400", description = "Dados inválidos na requisição")
    @APIResponse(responseCode = "401", description = "Usuário não autenticado")
    @APIResponse(responseCode = "429", description = "Limite de requisições excedido")
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
