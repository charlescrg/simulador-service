package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.repository.ProdutoExternoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@ApplicationScoped
@Tag(name = "Produtos", description = "Serviço para operações com produtos")
public class ProdutoService {

    private final ProdutoExternoRepository produtoExternoRepository;

    @Inject
    public ProdutoService(ProdutoExternoRepository produtoExternoRepository) {
        this.produtoExternoRepository = produtoExternoRepository;
    }

    @Transactional
    public ProdutoExternoEntity atualizar(Integer id, ProdutoExternoEntity produtoAtualizado) {
        ProdutoExternoEntity produto = produtoExternoRepository.findById(id);
        if (produto != null) {
            if (produtoAtualizado.getNoProduto() != null) {
                produto.setNoProduto(produtoAtualizado.getNoProduto());
            }
            if (produtoAtualizado.getPcTaxaJuros() != null) {
                produto.setPcTaxaJuros(produtoAtualizado.getPcTaxaJuros());
            }
            if (produtoAtualizado.getNuMinimoMeses() != null) {
                produto.setNuMinimoMeses(produtoAtualizado.getNuMinimoMeses());
            }
            if (produtoAtualizado.getNuMaximoMeses() != null) {
                produto.setNuMaximoMeses(produtoAtualizado.getNuMaximoMeses());
            }
            if (produtoAtualizado.getVrMinimo() != null) {
                produto.setVrMinimo(produtoAtualizado.getVrMinimo());
            }
            if (produtoAtualizado.getVrMaximo() != null) {
                produto.setVrMaximo(produtoAtualizado.getVrMaximo());
            }
            produtoExternoRepository.persist(produto);
        }
        return produto;
    }
}
