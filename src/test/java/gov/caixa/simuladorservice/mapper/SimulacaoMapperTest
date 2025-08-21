package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulacaoMapperTest {
    @Test
    void testCriarSimulacao() {
        SimulacaoMapper mapper = new SimulacaoMapper();
        SimulacaoRequestDto req = new SimulacaoRequestDto();
        ProdutoExternoEntity prod = new ProdutoExternoEntity();
        prod.setNoProduto("Produto");
        prod.setCoProduto(1);
        var simulacao = mapper.criarSimulacao(req, prod, 100L);
        assertEquals("Produto", simulacao.getProduto());
        assertEquals(1, simulacao.getCodigoProduto());
    }
}

