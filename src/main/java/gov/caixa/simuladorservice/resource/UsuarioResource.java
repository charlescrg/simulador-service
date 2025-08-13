package gov.caixa.simuladorservice.resource;

import gov.caixa.simuladorservice.entity.Usuario;
import gov.caixa.simuladorservice.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/usuarios")
@RolesAllowed("admin")
@Tag(name = "Usuários", description = "Operações relacionadas a usuários do sistema")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @GET
    @Operation(summary = "Lista todos os usuários")
    public List<Usuario> listar() {
        return usuarioService.listarTudo();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca usuário por ID")
    public Usuario buscar(@PathParam("id") Long id) {
        return usuarioService.buscarPorId(id);
    }

    @POST
    @Operation(summary = "Cria um novo usuário")
    public Usuario criar(Usuario usuario) {
        return usuarioService.salvar(usuario);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um usuário existente")
    public Usuario atualizar(@PathParam("id") Long id, Usuario usuario) {
        return usuarioService.atualizar(id, usuario);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remove um usuário")
    public void deletar(@PathParam("id") Long id) {
        usuarioService.deletar(id);
    }
}
