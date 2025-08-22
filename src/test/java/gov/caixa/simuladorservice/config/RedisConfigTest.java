package gov.caixa.simuladorservice.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RedisConfigTest {
    @Test
    void testConfigCriada() {
        RedisConfig config = new RedisConfig();
        assertNotNull(config);
    }
}

