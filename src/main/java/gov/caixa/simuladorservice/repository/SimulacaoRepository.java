package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepositoryBase<SimulacaoEntity, Integer> {

    public List<SimulacaoEntity> listarTodas() {
        return listAll();
    }

    public void salvar(SimulacaoEntity simulacao) {
        persist(simulacao);
    }
}