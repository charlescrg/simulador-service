package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class SimulacaoRepository {

    @PersistenceContext(unitName = "local")
    EntityManager em;

    public List<SimulacaoEntity> listarTodas() {
        TypedQuery<SimulacaoEntity> query = em.createQuery("SELECT s FROM Simulacao s", SimulacaoEntity.class);
        return query.getResultList();
    }

    public void salvar(SimulacaoEntity simulacao) {
        em.persist(simulacao);
    }
}
