package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.Usuario;

import java.util.Optional;

public interface  UsuarioRepository extends GenericRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
}

