package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.ResultadoSimulacaoDto;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SimuladorFinanceiroServiceTest {
    @Test
    void testCalcularSAC() {
        SimuladorFinanceiroService service = new SimuladorFinanceiroService();
        ResultadoSimulacaoDto resultado = service.calcularSAC(BigDecimal.valueOf(1000), BigDecimal.valueOf(0.01), 10);
        assertEquals("SAC", resultado.getTipo());
        assertEquals(10, resultado.getParcelas().size());
    }
    @Test
    void testCalcularPRICE() {
        SimuladorFinanceiroService service = new SimuladorFinanceiroService();
        ResultadoSimulacaoDto resultado = service.calcularPRICE(BigDecimal.valueOf(1000), BigDecimal.valueOf(0.01), 10);
        assertEquals(10, resultado.getParcelas().size());
    }
}

