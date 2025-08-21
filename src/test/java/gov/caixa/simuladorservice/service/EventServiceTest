package gov.caixa.simuladorservice.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import gov.caixa.simuladorservice.producer.EventHubProducer;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.concurrent.CompletableFuture;

class EventServiceTest {
    @Test
    void testEnviarEventoAsync() {
        EventHubProducer producer = mock(EventHubProducer.class);
        ManagedExecutor executor = mock(ManagedExecutor.class);
        when(executor.runAsync(any())).thenAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return CompletableFuture.completedFuture(null);
        });

        EventService service = new EventService();
        service.eventHubProducer = producer;
        service.executor = executor;

        service.enviarEvento("msg", "corrId");

        verify(producer).enviarEvento("msg", "corrId");
    }
}
