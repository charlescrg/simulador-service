package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TelemetriaRepository implements PanacheRepository<TelemetriaEntity> {

    public List<Object[]> listar() {
        return getEntityManager().createQuery(
                        "SELECT t.data, t.nomeApi, " +
                                "SUM(t.qtdRequisicoes), " +
                                "AVG(t.tempoMedio), " +
                                "MIN(t.tempoMinimo), " +
                                "MAX(t.tempoMaximo), " +
                                "CASE WHEN SUM(t.qtdRequisicoes) > 0 " +
                                "THEN SUM(t.qtdRequisicoes * t.percentualSucesso)/SUM(t.qtdRequisicoes) " +
                                "ELSE 0 END " +
                                "FROM TelemetriaEntity t " +
                                "GROUP BY t.data, t.nomeApi " +
                                "ORDER BY t.data, t.nomeApi", Object[].class)
                .getResultList();
    }
}
