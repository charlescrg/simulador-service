package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProdutoExternoRepository implements PanacheRepositoryBase<ProdutoExternoEntity, Integer> {

    public List<ProdutoExternoEntity> listarTodos() {
        return listAll();
    }
}
