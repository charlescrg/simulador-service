package br.com.charles.superapp.repository;

import br.com.charles.superapp.entity.Auditoria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuditoriaRepository implements PanacheRepository<Auditoria> {
}

