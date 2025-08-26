package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoTipoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public interface SimulacaoMapper {

    SimulacaoMapper INSTANCE = Mappers.getMapper(SimulacaoMapper.class);

    @Mapping(target = "dataSimulacao", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "produto", source = "produto.noProduto")
    @Mapping(target = "codigoProduto", source = "produto.coProduto")
    @Mapping(target = "valorSimulado", source = "request.valorDesejado")
    @Mapping(target = "valorTotalCredito", source = "request.valorDesejado")
    @Mapping(target = "tempoRespostaMs", source = "tempoRespostaMs")
    SimulacaoEntity criarSimulacao(SimulacaoRequestDto request, ProdutoExternoEntity produto, long tempoRespostaMs);

    @Mapping(target = "idSimulacao", source = "simulacao.id")
    @Mapping(target = "codigoProduto", source = "produto.coProduto")
    @Mapping(target = "descricaoProduto", source = "produto.noProduto")
    @Mapping(target = "taxaJuros", source = "produto.pcTaxaJuros")
    @Mapping(target = "resultadoSimulacao", expression = "java(java.util.List.of(sac, price))")
    SimulacaoResponseDto montarRetornoSimulacao(ProdutoExternoEntity produto, ResultadoSimulacaoDto sac, ResultadoSimulacaoDto price, SimulacaoEntity simulacao);

    @Mapping(target = "idSimulacao", expression = "java(Math.toIntExact(simulacao.getId()))")
    @Mapping(target = "valorDesejado", source = "simulacao.valorSimulado")
    @Mapping(target = "prazo", source = "simulacao.tipos", qualifiedByName = "extrairPrazo")
    @Mapping(target = "valorTotalParcelas", source = "simulacao.tipos", qualifiedByName = "mapearParcelas")
    SimulacaoResumoDto mapearParaResumo(SimulacaoEntity simulacao);

    @Named("extrairPrazo")
    default Integer extrairPrazo(List<SimulacaoTipoEntity> tipos) {
        if (tipos == null || tipos.isEmpty()) return null;
        return tipos.get(0).getPrazo() != null ? tipos.get(0).getPrazo().intValue() : null;
    }

    @Named("mapearParcelas")
    default List<ValorTotalParcelasTipoDto> mapearParcelas(List<SimulacaoTipoEntity> tipos) {
        if (tipos == null) return null;
        return tipos.stream().map(tipo -> {
            ValorTotalParcelasTipoDto dto = new ValorTotalParcelasTipoDto();
            dto.setTipo(tipo.getTipoSimulacao());
            dto.setValorTotalParcelas(tipo.getValorTotalParcelas());
            return dto;
        }).collect(Collectors.toList());
    }
}
