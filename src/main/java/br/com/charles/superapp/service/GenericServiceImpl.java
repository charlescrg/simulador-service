package br.com.charles.superapp.service;

import br.com.charles.superapp.repository.GenericRepository;
import jakarta.transaction.Transactional;

import java.util.List;

public abstract class GenericServiceImpl<T, ID> implements GenericService<T, ID> {

    protected final GenericRepository<T, ID> repository;

    public GenericServiceImpl() {
        this.repository = null;
    }

    public GenericServiceImpl(GenericRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> listarTudo() {
        return repository.listAll();
    }

    @Override
    public T buscarPorId(ID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public T salvar(T entidade) {
        repository.persist(entidade);
        return entidade;
    }

    @Override
    @Transactional
    public abstract T atualizar(ID id, T entidadeAtualizada);

    @Override
    @Transactional
    public boolean deletar(ID id) {
        return repository.deleteById(id);
    }
}

