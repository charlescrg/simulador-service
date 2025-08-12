package br.com.charles.superapp.service;

import br.com.charles.superapp.entity.Usuario;
import br.com.charles.superapp.security.JwtGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    UsuarioService usuarioService;

    @Inject
    JwtGenerator jwtGenerator;

    public String login(String login, String senha) {
        Optional<Usuario> usuarioOpt = usuarioService.findByLogin(login);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuarioService.validarPassword(usuario, senha)) {
            throw new RuntimeException("Senha inválida");
        }

        return jwtGenerator.generateToken(usuario);
    }
}
