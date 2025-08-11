package br.com.charles.superapp.infra.security;

import br.com.charles.superapp.entity.Usuario;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class JwtGenerator {

    public String generateToken(Usuario usuario) {
        return Jwt.issuer("https://my-app")
                .upn(usuario.getLogin())
                .groups(Set.of(usuario.getRole().getRole())) // roles aqui
                .expiresIn(3600) // 1 hora em segundos
                .sign();
    }
}
