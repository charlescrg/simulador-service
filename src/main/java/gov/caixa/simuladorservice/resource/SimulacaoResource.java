package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.dto.SimulacaoResponseDto;
import gov.caixa.simuladorservice.service.SimulacaoService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/simulacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Endpoint para simulação de empréstimos")
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @POST
    @Operation(summary = "Simula empréstimo com SAC e PRICE")
    @Authenticated

public Response simular(@Valid SimulacaoRequestDto request,
                        @Context HttpServletRequest httpRequest,
                        @Context SecurityContext securityContext) {

    String ip = httpRequest.getRemoteAddr();
    String usuario = securityContext.getUserPrincipal().getName();

    log.info("Simulação solicitada por usuário={} IP={} valor={} prazo={}",
             usuario, ip, request.getValorDesejado(), request.getPrazo());

    try {
        var resposta = simulacaoService.simular(request);
        log.info("Simulação concluída com sucesso para usuário={}", usuario);
        return Response.ok(resposta).build();
    } catch (Exception e) {
        log.error("Erro na simulação para usuário={} IP={}", usuario, ip, e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
