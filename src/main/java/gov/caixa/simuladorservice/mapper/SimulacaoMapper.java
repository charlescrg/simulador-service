package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

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
    SimulacaoResumoDto mapearParaResumo(SimulacaoEntity simulacao);
}
