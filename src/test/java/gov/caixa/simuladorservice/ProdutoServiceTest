package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.repository.ProdutoExternoRepository;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoServiceTest {
    @Test
    void testAtualizarProduto() {
        ProdutoExternoRepository repo = mock(ProdutoExternoRepository.class);
        ProdutoExternoEntity produto = new ProdutoExternoEntity();
        produto.setNoProduto("Antigo");
        when(repo.findById(1)).thenReturn(produto);
        ProdutoService service = new ProdutoService(repo);
        ProdutoExternoEntity atualizado = new ProdutoExternoEntity();
        atualizado.setNoProduto("Novo");
        ProdutoExternoEntity result = service.atualizar(1, atualizado);
        assertEquals("Novo", result.getNoProduto());
        verify(repo).persist(produto);
    }
}

