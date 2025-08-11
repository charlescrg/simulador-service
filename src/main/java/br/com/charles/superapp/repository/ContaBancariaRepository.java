package br.com.charles.superapp.repository;

import br.com.charles.superapp.entity.ContaBancaria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContaBancariaRepository implements PanacheRepository<ContaBancaria> {
}

