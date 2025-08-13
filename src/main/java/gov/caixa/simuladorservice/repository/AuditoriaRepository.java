package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.Auditoria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuditoriaRepository implements PanacheRepository<Auditoria> {
}

