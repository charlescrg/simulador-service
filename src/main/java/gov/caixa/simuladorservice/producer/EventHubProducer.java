package gov.caixa.simuladorservice.producer;

import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventData;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;

@ApplicationScoped
public class EventHubProducer {

    @Inject
    @ConfigProperty(name = "eventhub.connection-string")
    String connectionString;

    private final EventHubProducerClient producer;

    public EventHubProducer() {
        producer = new EventHubClientBuilder()
                .connectionString(connectionString)
                .buildProducerClient();
    }

    
    public void enviarEvento(String mensagemJson, String correlationId) {
            EventData eventData = new EventData(mensagemJson);
            eventData.getProperties().put("correlationId", correlationId);
    
            producer.send(Collections.singletonList(eventData));
    
            Logger.getLogger(EventHubProducer.class)
                  .infof("Evento enviado | correlationId=%s | payload=%s", correlationId, mensagemJson);
    }

}
