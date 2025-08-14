package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.Produto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class ProdutoRepository implements GenericRepository<Produto, Integer> {

    @PersistenceContext
    EntityManager em;

    public List<Produto> listarTodos() {
        TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p", Produto.class);
        return query.getResultList();
    }

    public Produto buscarPorCodigo(Long codigoProduto) {
        return em.find(Produto.class, codigoProduto);
    }
}
