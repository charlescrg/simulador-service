package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.exception.SimulacaoIndisponivelException;
import gov.caixa.simuladorservice.service.EventService;
import gov.caixa.simuladorservice.service.SimulacaoService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Path("/api/v1/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Endpoint para simulação de empréstimos")
public class SimulacaoResource {

    private static final Logger log = LoggerFactory.getLogger(SimulacaoResource.class);

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    EventService eventService;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                .build());
    }


    @POST
    @Operation(summary = "Simula empréstimo com SAC e PRICE")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Simulação realizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SimulacaoResponseDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Requisição com formato inválido",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ErroValidacaoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Acesso não permitido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "429",
                    description = "Limite de requisições excedido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            )
    })
    @Authenticated
        public Response simular(@Valid SimulacaoRequestDto request,
                            @Context UriInfo uriInfo,
                            @Context SecurityContext securityContext,
                            @Context HttpHeaders headers,
                            @HeaderParam("X-Correlation-Id") String correlationId) {

        String correlationIdFinal = rastrearCorrelationId(correlationId);
        String ipCliente = extrairIpCliente(headers, uriInfo);
        String usuario = securityContext.getUserPrincipal().getName();

        if (!verificarLimiteRequisicoes(ipCliente)) {
            log.warn("Rate limit excedido para IP={} usuário={}", ipCliente, usuario);
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("Limite de requisições excedido. Tente novamente mais tarde.")
                    .build();
        }

        log.info("Simulação solicitada por: usuário={}, IP={}, valor={}, prazo={}, correlationId={}",
                usuario, ipCliente, request.getValorDesejado(), request.getPrazo(), correlationIdFinal);

        try {
            SimulacaoResponseDto resposta = simulacaoService.simular(request, correlationIdFinal); //TODO ver auditoria
            log.info("Simulação concluída com sucesso para usuário={}", usuario);

            Response response = Response.ok(resposta).build();
            enviarEvento(resposta.toString(), correlationIdFinal);

            return Response.ok(response).build();

        } catch (SimulacaoIndisponivelException e) {
            log.warn("Fallback acionado: {}", e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Erro na simulação para usuário={} IP={}", usuario, ipCliente, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao processar a simulação").build();
        }
    }

    @GET
    @Path("/listar")
    @Operation(summary = "Lista todas as simulações realizadas")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Lista de simulações realizadas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ListaSimulacoesResponseDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Acesso não permitido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao listar simulações",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            )
    })
    @Authenticated
    public Response listarSimulacoes() {
        try {
            ListaSimulacoesResponseDto resposta = simulacaoService.listarSimulacoes();
            return Response.ok(resposta).build();
        } catch (Exception e) {
            log.error("Erro ao listar simulações", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar simulações").build();
        }
    }

    @GET
    @Path("/valores-por-produto-dia")
    @Operation(summary = "Retorna os valores simulados agrupados por produto e dia")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Valores simulados agrupados por produto e data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VolumeSimuladoResponseDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Acesso não permitido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno ao listar simulações",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            )
    })
    @Authenticated
    public Response listarValoresPorProdutoDia() {
        try {
            List<VolumeSimuladoResponseDto> resposta = simulacaoService.listarValoresPorProdutoDia();
            return Response.ok(resposta).build();
        } catch (Exception e) {
            log.error("Erro ao listar valores simulados por produto/dia", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar valores simulados por produto/dia").build();
        }
    }

      private String rastrearCorrelationId(String correlationId) {
        return Optional.ofNullable(correlationId)
                .filter(id -> !id.isBlank())
                .orElse(UUID.randomUUID().toString());
    }

    private String extrairIpCliente(HttpHeaders headers, UriInfo uriInfo) {
        return Optional.ofNullable(headers.getHeaderString("X-Forwarded-For"))
                .orElse(uriInfo.getRequestUri().getHost());
    }

    private boolean verificarLimiteRequisicoes(String ipCliente) {
        Bucket bucket = resolveBucket(ipCliente);
        return bucket.tryConsume(1);
    }

    private void enviarEvento(String mensagemJson, String correlationId) {
        CompletableFuture.runAsync(() -> {
            try {
                eventService.enviarEvento(mensagemJson, correlationId);
            } catch (Exception e) {
                log.warn("Falha ao enviar evento de simulação (não afeta resposta ao cliente): {}", e.getMessage());
            }
        });
    }

}
