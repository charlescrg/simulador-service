package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.ResultadoSimulacaoDto;
import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.dto.SimulacaoResponseDto;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
        Produto produto = Produto.builder()
                .coProduto(1)
                .noProduto("Produto 1")
                .pcTaxaJuros(new BigDecimal("0.02"))
                .nuMinimoMeses((short) 1)
                .nuMaximoMeses((short) 12)
                .vrMinimo(new BigDecimal("1000"))
                .vrMaximo(new BigDecimal("10000"))
                .build();

        doReturn(List.of(produto)).when(produtoRepository).listarTodos();

        SimulacaoRequestDto request = new SimulacaoRequestDto();
        request.setValorDesejado(new BigDecimal("5000"));
        request.setPrazo(10);

        String correlationId = UUID.randomUUID().toString();

        SimulacaoResponseDto response = simulacaoService.simular(request, correlationId);

        assertNotNull(response);
        assertEquals(1L, response.getCodigoProduto());
        assertEquals("Produto 1", response.getDescricaoProduto());
        assertFalse(response.getResultadoSimulacao().isEmpty());
    }

    @Test
    void deveChamarFallbackQuandoFalha() {
        SimulacaoRequestDto request = new SimulacaoRequestDto();
        request.setValorDesejado(BigDecimal.valueOf(1000));
        request.setPrazo(12);

        when(produtoRepository.listarTodos()).thenThrow(new RuntimeException());

        String correlationId = UUID.randomUUID().toString();
        SimulacaoResponseDto response = simulacaoService.simular(request, correlationId);

        assertEquals("Simulação indisponível no momento. Sua solicitação será reprocessada.", response.getDescricaoProduto());
        assertTrue(response.getResultadoSimulacao().isEmpty());
    }

    @Test
    void deveCalcularSACCorretamente() {
        Produto produto = Produto.builder()
                .coProduto(1)
                .noProduto("Produto 1")
                .pcTaxaJuros(new BigDecimal("0.02"))
                .nuMinimoMeses((short) 1)
                .nuMaximoMeses((short) 12)
                .vrMinimo(new BigDecimal("1000"))
                .vrMaximo(new BigDecimal("10000"))
                .build();

        doReturn(List.of(produto)).when(produtoRepository).listarTodos();

        BigDecimal valor = new BigDecimal("1000");
        int prazo = 5;
        SimulacaoRequestDto request = new SimulacaoRequestDto();
        request.setValorDesejado(valor);
        request.setPrazo(prazo);

        String correlationId = UUID.randomUUID().toString();

        ResultadoSimulacaoDto sac = simulacaoService.simular(request, correlationId)
                .getResultadoSimulacao().stream()
                .filter(r -> "SAC".equals(r.getTipo()))
                .findFirst()
                .orElseThrow();

        assertEquals(prazo, sac.getParcelas().size());
        assertEquals(new BigDecimal("200.00"), sac.getParcelas().get(0).getValorAmortizacao());
    }
}
