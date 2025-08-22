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
}
