package br.com.charles.superapp.repository;


import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public interface GenericRepository<T, ID> extends PanacheRepositoryBase<T, ID> {
    // Pode deixar vazio, pois já herda métodos do PanacheRepositoryBase
}
