package gov.caixa.simuladorservice.producer;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventHubProducer {

    private static final Logger LOG = Logger.getLogger(EventHubProducer.class);

    public void enviarEvento(String mensagemJson) {
        // Aqui iria a integração real com Azure EventHub
        LOG.info("Evento enviado para EventHub: " + mensagemJson);
    }
}
