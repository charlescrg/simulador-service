package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.ContaBancaria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContaBancariaRepository implements PanacheRepository<ContaBancaria> {
}

