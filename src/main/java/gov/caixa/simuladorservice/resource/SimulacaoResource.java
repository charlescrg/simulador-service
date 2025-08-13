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

@Path("/simulacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Endpoint para simulação de empréstimos")
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @POST
    @Operation(summary = "Simula empréstimo com SAC e PRICE")
    @Authenticated
    public Response simular(@Valid SimulacaoRequestDto request) {
        try {
            SimulacaoResponseDto response = simulacaoService.simular(request);
            return Response.ok(response).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno: " + ex.getMessage()).build();
        }
    }
}
