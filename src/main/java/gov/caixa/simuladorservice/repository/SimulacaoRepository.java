package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.Simulacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class SimulacaoRepository {

    @PersistenceContext
    EntityManager em;

    public List<Simulacao> listarTodas() {
        TypedQuery<Simulacao> query = em.createQuery("SELECT s FROM Simulacao s", Simulacao.class);
        return query.getResultList();
    }

    public void salvar(Simulacao simulacao) {
        em.persist(simulacao);
    }
}
