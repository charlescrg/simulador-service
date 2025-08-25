package gov.caixa.simuladorservice.metric;

import gov.caixa.simuladorservice.service.TelemetriaService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.time.Duration;

@Provider
@Priority(1)
@ApplicationScoped
public class MetricInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    TelemetriaService telemetriaService;

    private static final String START_TIME = "start-time";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(START_TIME, System.nanoTime());
    }

    @Override
    @Transactional
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        long start = (long) requestContext.getProperty(START_TIME);
        long durationNanos = System.nanoTime() - start;
        long tempoMs = Duration.ofNanos(durationNanos).toMillis();

        String path = requestContext.getUriInfo().getPath();
        boolean sucesso = responseContext.getStatus() >= 200 && responseContext.getStatus() < 300;

        telemetriaService.salvar(path, tempoMs, sucesso);
    }
}
