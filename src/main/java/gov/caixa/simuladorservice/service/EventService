package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.producer.EventHubProducer;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.context.ManagedExecutor;
import jakarta.inject.Inject;

@ApplicationScoped
public class EventService {

    @Inject
    EventHubProducer eventHubProducer;

    @Inject
    ManagedExecutor executor;

    public void enviarEvento(String mensagemJson, String correlationId) {
        executor.runAsync(() -> {
            eventHubProducer.enviarEvento(mensagemJson, correlationId);
        });
    }
}

