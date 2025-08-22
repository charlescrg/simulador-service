package gov.caixa.simuladorservice.producer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventHubProducerTest {
    @Test
    void testConstrutor() {
        EventHubProducer producer = new EventHubProducer();
        assertNotNull(producer);
    }
}

