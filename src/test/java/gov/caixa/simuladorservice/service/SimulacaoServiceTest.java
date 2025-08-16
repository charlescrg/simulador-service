package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.ResultadoSimulacaoDto;
import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.entity.Produto;
import gov.caixa.simuladorservice.producer.EventHubProducer;
import gov.caixa.simuladorservice.repository.ProdutoRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
class SimulacaoServiceTest {

    @Inject
    SimulacaoService simulacaoService;

    @InjectSpy
    ProdutoRepository produtoRepository;

    @InjectMock
    EventHubProducer eventHubProducer;

    @Test
    void deveRetornarSimulacaoValida() {
        // Preparar produto mock
        Produto produto = Produto.builder()
                .coProduto(1)
                .noProduto("Produto 1")
                .pcTaxaJuros(new BigDecimal("0.02"))
                .nuMinimoMeses((short) 1)
                .nuMaximoMeses((short) 12)
                .vrMinimo(new BigDecimal("1000"))
                .vrMaximo(new BigDecimal("10000"))
                .build();

        // Mock do método listarTodos usando spy
        doReturn(List.of(produto)).when(produtoRepository).listarTodos();

        SimulacaoRequestDto request = new SimulacaoRequestDto();
        request.setValorDesejado(new BigDecimal("5000"));
        request.setPrazo(10);

        var response = simulacaoService.simular(request);

        assertNotNull(response);
        assertEquals(1L, response.getCodigoProduto());
        assertEquals("Produto 1", response.getDescricaoProduto());
    }


    @Test
    void deveLancarExcecaoQuandoNenhumProdutoCompatível() {
        // Retorna lista vazia para simular que nenhum produto atende aos critérios
        doReturn(List.of()).when(produtoRepository).listarTodos();

        SimulacaoRequestDto request = new SimulacaoRequestDto();
        request.setValorDesejado(new BigDecimal("5000"));
        request.setPrazo(10);

        // Espera que lance IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> simulacaoService.simular(request));
    }

    @Test
    void deveCalcularSACCorretamente() throws Exception {
        BigDecimal valor = new BigDecimal("1000");
        BigDecimal taxa = new BigDecimal("0.02");
        int prazo = 5;

        ResultadoSimulacaoDto sac = simulacaoService.simular(
                        new SimulacaoRequestDto(valor, prazo)
                ).getResultadoSimulacao().stream()
                .filter(r -> "SAC".equals(r.getTipo()))
                .findFirst()
                .orElseThrow();

        // Verifica quantidade de parcelas
        assertEquals(prazo, sac.getParcelas().size());
        // Checagem simples do valor da primeira parcela
        assertEquals(new BigDecimal("200.00"), sac.getParcelas().get(0).getValorAmortizacao());
    }
}
