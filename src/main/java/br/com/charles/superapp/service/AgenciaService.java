package br.com.charles.superapp.service;

import br.com.charles.superapp.entity.Agencia;
import br.com.charles.superapp.repository.AgenciaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AgenciaService extends GenericServiceImpl<Agencia, Long> {

    @Inject
    AgenciaRepository agenciaRepository;

    public AgenciaService() {}

    @Inject
    public AgenciaService(AgenciaRepository agenciaRepository) {
        super(agenciaRepository);
    }

    @Override
    @Transactional
    public Agencia atualizar(Long id, Agencia agenciaAtualizada) {
        Agencia agencia = agenciaRepository.findById(id);
        if (agencia != null) {
            // Exemplo de regra: não atualizar se nome for vazio
            if (agenciaAtualizada.getNome() != null && !agenciaAtualizada.getNome().isEmpty()) {
                agencia.setNome(agenciaAtualizada.getNome());
            }
            agencia.setNumero(agenciaAtualizada.getNumero());
            agencia.setEndereco(agenciaAtualizada.getEndereco());
        }
        return agencia;
    }

    // Aqui você pode colocar outros métodos específicos para regras da Agencia

}
