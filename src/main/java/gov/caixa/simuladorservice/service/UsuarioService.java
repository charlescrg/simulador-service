package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.RegisterDto;
import gov.caixa.simuladorservice.entity.Usuario;
import gov.caixa.simuladorservice.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@ApplicationScoped
public class UsuarioService extends GenericServiceImpl<Usuario, Long> {

    @Inject
    UsuarioRepository usuarioRepository;

    public UsuarioService() {}

    @Inject
    public UsuarioService(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
    }

    public Optional<Usuario> findByLogin(String login) {
        return (usuarioRepository).findByLogin(login);
    }

    public boolean validarPassword(Usuario usuario, String rawPassword) {
        return BCrypt.checkpw(rawPassword, usuario.getSenha());
    }

    public void registrarUsuario(RegisterDto data) {
        if (findByLogin(data.getLogin()).isPresent()) {
            throw new RuntimeException("Usuário já existe");
        }

        String senhaCriptografada = BcryptUtil.bcryptHash(data.getPassword());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(data.getLogin());
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setRole(data.getRole());
        novoUsuario.setAtivo(true);
        novoUsuario.setNotificacoesAtivas(true);

        salvar(novoUsuario);
    }

    @Override
    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id);

        if (usuario != null) {
            if (usuarioAtualizado.getLogin() != null && !usuarioAtualizado.getLogin().isEmpty()) {
                usuario.setLogin(usuarioAtualizado.getLogin());
            }
            if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
                usuario.setSenha(usuarioAtualizado.getSenha()); // criptografar no futuro
            }
            if (usuarioAtualizado.getNomeCompleto() != null && !usuarioAtualizado.getNomeCompleto().isEmpty()) {
                usuario.setNomeCompleto(usuarioAtualizado.getNomeCompleto());
            }
            if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().isEmpty()) {
                usuario.setEmail(usuarioAtualizado.getEmail());
            }
            if (usuarioAtualizado.getPerfil() != null) {
                usuario.setPerfil(usuarioAtualizado.getPerfil());
            }
            if (usuarioAtualizado.getRendaMensal() != null) {
                usuario.setRendaMensal(usuarioAtualizado.getRendaMensal());
            }
        }

        return usuario;
    }

}
