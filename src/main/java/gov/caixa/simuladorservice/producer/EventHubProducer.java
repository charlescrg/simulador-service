package gov.caixa.simuladorservice.producer;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.core.amqp.exception.AmqpException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@ApplicationScoped
public class EventHubProducer {

    private static final Logger LOG = Logger.getLogger(EventHubProducer.class);

    @Inject
    @ConfigProperty(name = "eventhub.connection-string")
    String connectionString;

    @Inject
    JedisPool jedisPool;

    private EventHubProducerClient producer;

    @PostConstruct
    void init() {
        try {
            producer = new EventHubClientBuilder()
                    .connectionString(connectionString)
                    .buildProducerClient();
            LOG.info("EventHubProducer inicializado com sucesso");
        } catch (Exception e) {
            LOG.error("Falha ao inicializar EventHubProducer", e);
            producer = null;
        }
    }

    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.75,
            delay = 5000,
            successThreshold = 2
    )
    @Fallback(fallbackMethod = "fallbackEnviarEvento")
    public void enviarEvento(String mensagemJson, String correlationId) {
        if (producer == null) {
            LOG.warnf("EventHubProducer não inicializado, evento não enviado | correlationId=%s", correlationId);
            return;
        }
        if (isDuplicate(correlationId)) {
            LOG.warnf("Evento duplicado detectado, não enviado | correlationId=%s", correlationId);
            return;
        }
        try {
            EventData eventData = new EventData(mensagemJson);
            eventData.getProperties().put("correlationId", correlationId);

            producer.send(Collections.singletonList(eventData));
            storeCorrelationId(correlationId);

            LOG.infof("Evento enviado | correlationId=%s | payload=%s", correlationId, mensagemJson);
        } catch (AmqpException e) {
            LOG.warnf(e, "Falha ao enviar evento no Event Hub | correlationId=%s", correlationId);
        } catch (Exception e) {
            LOG.errorf(e, "Erro inesperado ao enviar evento | correlationId=%s", correlationId);
        }
    }

    private boolean isDuplicate(String correlationId) {
        try (var jedis = jedisPool.getResource()) {
            return jedis.exists(correlationId);
        }
    }

    private void storeCorrelationId(String correlationId) {
        try (var jedis = jedisPool.getResource()) {
            jedis.setex(correlationId, 600, "1"); // Expira em 10 minutos
        }
    }

    @PreDestroy
    void shutdown() {
        if (producer != null) {
            try {
                producer.close();
                LOG.info("EventHubProducer finalizado com sucesso");
            } catch (Exception e) {
                LOG.warn("Erro ao fechar EventHubProducer", e);
            }
        }
    }

    public void fallbackEnviarEvento(String mensagemJson, String correlationId, Throwable t) {
        // Pega a causa raiz da exceção
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        String erroMsg = cause.getMessage();

        LOG.warnf("Fallback ativado: evento não enviado | correlationId=%s | erro=%s",
                correlationId, erroMsg);
    }
}
