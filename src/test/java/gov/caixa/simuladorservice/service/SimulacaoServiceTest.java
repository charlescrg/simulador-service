package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import gov.caixa.simuladorservice.exception.SimulacaoIndisponivelException;
import gov.caixa.simuladorservice.mapper.SimulacaoMapper;
import gov.caixa.simuladorservice.repository.ProdutoExternoRepository;
import gov.caixa.simuladorservice.repository.SimulacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoServiceTest {
    @Mock
    SimulacaoRepository simulacaoRepository;
    @Mock
    SimuladorFinanceiroService simuladorFinanceiroService;
    @Mock
    SimulacaoMapper simulacaoMapper;
    @Mock
    ProdutoExternoRepository produtoExternoRepository;

    @InjectMocks
    SimulacaoService simulacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSimularProdutoNaoEncontrado() {
        SimulacaoRequestDto request = new SimulacaoRequestDto();
        when(produtoExternoRepository.listarTodos()).thenReturn(Collections.emptyList());
        SimulacaoResponseDto response = simulacaoService.simular(request, "corr-id");
        assertEquals("Nenhum produto compatível encontrado.", response.getDescricaoProduto());
    }

    @Test
    void testSimularFallback() {
        assertThrows(SimulacaoIndisponivelException.class, () -> {
            simulacaoService.simularFallback(new SimulacaoRequestDto(), "corr-id");
        });
    }

    @Test
    void testBuscarProdutoRetornaNull() {
        SimulacaoRequestDto request = new SimulacaoRequestDto();
        when(produtoExternoRepository.listarTodos()).thenReturn(Collections.emptyList());
        ProdutoExternoEntity produto = simulacaoService.buscarProduto(request);
        assertNull(produto);
    }
}

