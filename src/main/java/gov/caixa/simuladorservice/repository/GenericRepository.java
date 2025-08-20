package gov.caixa.simuladorservice.repository;


import io.quarkus.agroal.DataSource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@DataSource("local")
public interface GenericRepository<T, ID> extends PanacheRepositoryBase<T, ID> {
    // Pode deixar vazio, pois já herda métodos do PanacheRepositoryBase
}
