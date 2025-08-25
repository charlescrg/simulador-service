package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TelemetriaRepository implements PanacheRepository<TelemetriaEntity> {

    public List<Object[]> listar() {
        String jpql = """
            SELECT t.data, t.nomeApi,
                   COUNT(t) as qtdRequisicoes,
                   AVG(t.tempoMs),
                   MIN(t.tempoMs),
                   MAX(t.tempoMs),
                   SUM(t.sucesso) * 1.0 / COUNT(t) * 100.0
            FROM TelemetriaEntity t
            GROUP BY t.data, t.nomeApi
            ORDER BY t.data, t.nomeApi
            """;

        return getEntityManager()
                .createQuery(jpql, Object[].class)
                .getResultList();
    }
}
