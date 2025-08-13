package br.com.charles.superapp.resource;

import br.com.charles.superapp.dto.LoginRequestDto;
import br.com.charles.superapp.dto.LoginResponseDto;
import br.com.charles.superapp.dto.RegisterDto;
import br.com.charles.superapp.service.AuthenticationService;
import br.com.charles.superapp.service.UsuarioService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    AuthenticationService authenticationService;

    @POST
    @Path("/login")
    @Transactional
    @PermitAll
    @Operation(summary = "Realiza login e retorna token JWT")
    public Response login(LoginRequestDto loginRequestDto) {
        try {
            String token = authenticationService.login(loginRequestDto.getLogin(), loginRequestDto.getPassword());
            return Response.ok(new LoginResponseDto(token)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }


    @POST
    @Path("/register")
    @Transactional
    @PermitAll
    @Operation(summary = "Registra um novo usuário no sistema")
    public Response register(@Valid RegisterDto data) {

        try {
            usuarioService.registrarUsuario(data);
            return Response.status(Response.Status.CREATED).entity("Usuário criado com sucesso").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
