package br.com.charles.superapp.repository;

import br.com.charles.superapp.entity.Usuario;

import java.util.Optional;

public interface  UsuarioRepository extends GenericRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
}

