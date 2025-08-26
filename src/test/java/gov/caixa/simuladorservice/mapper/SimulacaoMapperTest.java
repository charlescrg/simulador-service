package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoMapperTest {

    private final SimulacaoMapper mapper = Mappers.getMapper(SimulacaoMapper.class);

    @Test
    void testCriarSimulacao() {
        // Arrange
        SimulacaoRequestDto req = new SimulacaoRequestDto();
        req.setValorDesejado(BigDecimal.valueOf(1000));

        ProdutoExternoEntity prod = new ProdutoExternoEntity();
        prod.setNoProduto("Produto");
        prod.setCoProduto(1);
        prod.setPcTaxaJuros(BigDecimal.valueOf(5.5));

        // Act
        SimulacaoEntity simulacao = mapper.criarSimulacao(req, prod, 100L);

        // Assert
        assertNotNull(simulacao, "Simulação não deve ser nula");
        assertEquals("Produto", simulacao.getProduto());
        assertEquals(1, simulacao.getCodigoProduto());
        assertEquals(BigDecimal.valueOf(1000), simulacao.getValorSimulado());
        assertEquals(BigDecimal.valueOf(1000), simulacao.getValorTotalCredito());
        assertEquals(100L, simulacao.getTempoRespostaMs());
        assertNotNull(simulacao.getDataSimulacao(), "Data da simulação não deve ser nula");
        assertEquals(LocalDate.now(), simulacao.getDataSimulacao(), "Data da simulação deve ser hoje");
    }
}
