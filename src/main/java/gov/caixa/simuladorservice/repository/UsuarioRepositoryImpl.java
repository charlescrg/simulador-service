package gov.caixa.simuladorservice.repository;

import gov.caixa.simuladorservice.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UsuarioRepositoryImpl implements UsuarioRepository {

    @Override
    public Optional<Usuario> findByLogin(String login) {
        return find("login", login).firstResultOptional();
    }

}
