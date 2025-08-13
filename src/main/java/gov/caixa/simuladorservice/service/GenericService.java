package gov.caixa.simuladorservice.service;

import java.util.List;

public interface GenericService<T, ID> {

    List<T> listarTudo();

    T buscarPorId(ID id);

    T salvar(T entidade);

    T atualizar(ID id, T entidadeAtualizada);

    boolean deletar(ID id);

}
