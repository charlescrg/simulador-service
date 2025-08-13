package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.entity.Produto;
import gov.caixa.simuladorservice.repository.ProdutoRepository;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Tag(name = "Produtos", description = "Serviço para operações com produtos")
public class ProdutoService extends GenericServiceImpl<Produto, Integer> {

    @Inject
    public ProdutoService(ProdutoRepository produtoRepository) {
        super(produtoRepository);
    }

    @Override
    @Transactional
    public Produto atualizar(Integer id, Produto produtoAtualizado) {
        Produto produto = repository.findById(id);
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
        }
        return produto;
    }
}
