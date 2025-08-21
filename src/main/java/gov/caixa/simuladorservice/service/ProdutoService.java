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

    @Transactional
    public void sincronizarProdutosExternos() {
        List<ProdutoExternoEntity> produtosExternos = produtoExternoRepository.listarTodos();
        for (ProdutoExternoEntity produtoExternoEntity : produtosExternos) {
            if (produtoExternoRepository.findById(produtoExternoEntity.getCoProduto()) == null) {
                ProdutoExternoEntity produto = ProdutoExternoEntity.builder()
                        .coProduto(produtoExternoEntity.getCoProduto())
                        .noProduto(produtoExternoEntity.getNoProduto())
                        .pcTaxaJuros(produtoExternoEntity.getPcTaxaJuros())
                        .nuMinimoMeses(produtoExternoEntity.getNuMinimoMeses())
                        .nuMaximoMeses(produtoExternoEntity.getNuMaximoMeses())
                        .vrMinimo(produtoExternoEntity.getVrMinimo())
                        .vrMaximo(produtoExternoEntity.getVrMaximo())
                        .build();
                produtoExternoRepository.persist(produto);
            }
        }
    }
}
