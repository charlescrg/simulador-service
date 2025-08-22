package gov.caixa.simuladorservice.metric;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetricInterceptorTest {
    @Test
    void testInstancia() {
        MetricInterceptor interceptor = new MetricInterceptor();
        assertNotNull(interceptor);
    }
}

