package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.dto.TelemetriaResponseDto;
import gov.caixa.simuladorservice.telemetria.TelemetriaInterceptor;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/telemetria")
@Produces(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    @Inject
    TelemetriaInterceptor interceptor;

    @GET
    public TelemetriaResponseDto getTelemetria() {
        return interceptor.gerarRelatorio();
    }
}
