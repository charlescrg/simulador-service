package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.dto.ErroPadraoDto;
import gov.caixa.simuladorservice.dto.TelemetriaResponseDto;
import gov.caixa.simuladorservice.service.TelemetriaService;
import io.quarkus.security.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.List;

@Path("/api/v1/telemetria")
@Produces(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    @GET
    @Path("/listar")
    @Operation(summary = "Lista telemetria de endpoints e persiste no banco")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Relatório de telemetria gerado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TelemetriaResponseDto.class)
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
                    description = "Erro interno ao gerar telemetria",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErroPadraoDto.class)
                    )
            )
    })
    @Authenticated
    public List<TelemetriaResponseDto> getTelemetria() {
        return telemetriaService.listarMetricas();
    }
}
