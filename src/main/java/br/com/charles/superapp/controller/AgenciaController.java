package br.com.charles.superapp.controller;

import br.com.charles.superapp.entity.Agencia;
import br.com.charles.superapp.service.AgenciaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/agencias")
@Tag(name = "Agências", description = "Operações relacionadas a agências bancárias")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgenciaController {

    @Inject
    AgenciaService agenciaService;

    @GET
    @Operation(summary = "Lista todas as agências")
    public List<Agencia> listar() {
        return agenciaService.listarTudo();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca agência por ID")
    public Agencia buscar(@PathParam("id") Long id) {
        return agenciaService.buscarPorId(id);
    }

    @POST
    @Operation(summary = "Cria uma nova agência")
    public Agencia criar(Agencia agencia) {
        return agenciaService.salvar(agencia);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza uma agência existente")
    public Agencia atualizar(@PathParam("id") Long id, Agencia agencia) {
        return agenciaService.atualizar(id, agencia);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remove uma agência")
    public void deletar(@PathParam("id") Long id) {
        agenciaService.deletar(id);
    }
}

