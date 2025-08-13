package gov.caixa.simuladorservice.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import javax.management.relation.Role;

@ApplicationScoped
public class RoleRepository implements PanacheRepository<Role> {
}

