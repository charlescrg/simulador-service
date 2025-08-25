package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.exception.ProdutoNaoEncontradoException;
import gov.caixa.simuladorservice.mapper.SimulacaoMapper;
import gov.caixa.simuladorservice.repository.ProdutoExternoRepository;
import gov.caixa.simuladorservice.repository.SimulacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        request.setPrazo(12);

        // Mock do repositório para retornar lista vazia
        when(produtoExternoRepository.listarTodos()).thenReturn(Collections.emptyList());

        // Verifica se a exceção é lançada
        ProdutoNaoEncontradoException exception = assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> simulacaoService.simular(request, "corr-id")
        );

        assertEquals("Desculpe! Nenhum produto compatível encontrado.", exception.getMessage());
    }

    @Test
    void testBuscarProdutoRetornaNull() {
        SimulacaoRequestDto request = new SimulacaoRequestDto();
        when(produtoExternoRepository.listarTodos()).thenReturn(Collections.emptyList());
        ProdutoExternoEntity produto = simulacaoService.buscarProduto(request);
        assertNull(produto);
    }
}

