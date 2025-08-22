package gov.caixa.simuladorservice.config;

import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TracingConfigTest {
    @Test
    void testGetTracer() {
        Tracer tracer = TracingConfig.getTracer();
        assertNotNull(tracer);
    }
}

