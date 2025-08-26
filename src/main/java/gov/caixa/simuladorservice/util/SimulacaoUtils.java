package gov.caixa.simuladorservice.util;

import gov.caixa.simuladorservice.dto.SimulacaoProdutoDto;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoTipoEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SimulacaoUtils {

    private SimulacaoUtils() {}

    public static Map<LocalDate, Map<String, List<SimulacaoEntity>>> agruparPorDataEProduto(List<SimulacaoEntity> simulacoes) {
        return simulacoes.stream()
                .collect(Collectors.groupingBy(
                        SimulacaoEntity::getDataSimulacao,
                        Collectors.groupingBy(s -> s.getCodigoProduto() + ":" + s.getProduto())
                ));
    }

    public static List<SimulacaoTipoEntity> filtrarTipos(List<SimulacaoEntity> simulacoes, String tipo) {
        return simulacoes.stream()
                .flatMap(s -> s.getTipos().stream())
                .filter(t -> tipo.equalsIgnoreCase(t.getTipoSimulacao()))
                .toList();
    }

    public static SimulacaoProdutoDto montarDtoProduto(String nomeProduto, List<SimulacaoEntity> simulacoesProduto,
                                                       BigDecimal taxaMediaJuroSAC, BigDecimal taxaMediaJuroPRICE,
                                                       BigDecimal valorMedioPrestacaoSAC, BigDecimal valorMedioPrestacaoPRICE,
                                                       BigDecimal valorTotalCreditoSAC, BigDecimal valorTotalCreditoPRICE,
                                                       BigDecimal valorTotalDesejado, Integer codigoProduto) {

        return SimulacaoProdutoDto.builder()
                .codigoProduto(codigoProduto)
                .descricaoProduto(nomeProduto)
                .taxaMediaJuroSAC(taxaMediaJuroSAC)
                .taxaMediaJuroPRICE(taxaMediaJuroPRICE)
                .valorMedioPrestacaoSAC(valorMedioPrestacaoSAC)
                .valorMedioPrestacaoPRICE(valorMedioPrestacaoPRICE)
                .valorTotalCreditoSAC(valorTotalCreditoSAC)
                .valorTotalCreditoPRICE(valorTotalCreditoPRICE)
                .valorTotalDesejado(valorTotalDesejado)
                .build();
    }

    public static BigDecimal arredondar(BigDecimal valor, int casas) {
        if (valor == null) return BigDecimal.ZERO;
        return valor.setScale(casas, BigDecimal.ROUND_HALF_UP);
    }
}
