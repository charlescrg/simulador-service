package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.Produto;
import gov.caixa.simuladorservice.producer.EventHubProducer;
import gov.caixa.simuladorservice.repository.ProdutoRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class SimulacaoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    EventHubProducer eventHubProducer;

    private final List<SimulacaoProdutoDto> simulacoesRealizadas = Collections.synchronizedList(new ArrayList<>());
    private final List<SimulacaoResumoDto> simulacoesHistorico = Collections.synchronizedList(new ArrayList<>());


    @CacheResult(cacheName = "simulacoes")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 1, delay = 5000)
    @Fallback(fallbackMethod = "simularFallback")
    public SimulacaoResponseDto simular(SimulacaoRequestDto request, String correlationId) {

        // Buscar produto compatível
        Produto produto = produtoRepository.listarTodos().stream()
                .filter(p -> request.getValorDesejado().compareTo(p.getVrMinimo()) >= 0
                        && (p.getVrMaximo() == null || request.getValorDesejado().compareTo(p.getVrMaximo()) <= 0)
                        && request.getPrazo() >= p.getNuMinimoMeses()
                        && (p.getNuMaximoMeses() == null || request.getPrazo() <= p.getNuMaximoMeses()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum produto compatível encontrado para os parâmetros informados."));

        // Calcular SAC e PRICE
        ResultadoSimulacaoDto sac = calcularSAC(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());
        ResultadoSimulacaoDto price = calcularPRICE(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());

        // Montar resposta
        SimulacaoResponseDto response = new SimulacaoResponseDto();
        response.setCodigoProduto(produto.getCoProduto().longValue());
        response.setDescricaoProduto(produto.getNoProduto());
        response.setTaxaJuros(produto.getPcTaxaJuros());
        response.setResultadoSimulacao(List.of(sac, price));

        // Enviar para EventHub
        eventHubProducer.enviarEvento(response.toString(), correlationId);
        log.info(String.format("Evento enviado para EventHub | correlationId=%s | payload=%s", correlationId, response));


        // TODO: refatorar

        SimulacaoProdutoDto produtoSimulado = new SimulacaoProdutoDto();
        produtoSimulado.setCodigoProduto(produto.getCoProduto());
        produtoSimulado.setDescricaoProduto(produto.getNoProduto());
        produtoSimulado.setTaxaMediaJuro(produto.getPcTaxaJuros());

        // Valor médio da prestação considerando SAC e PRICE
                BigDecimal valorMedioPrestacao = (sac.getParcelas().stream()
                        .map(ParcelaDto::getValorPrestacao)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .add(
                                price.getParcelas().stream()
                                        .map(ParcelaDto::getValorPrestacao)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        ))
                        .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

                produtoSimulado.setValorMedioPrestacao(valorMedioPrestacao);

        // Valor total desejado é o valor que o cliente solicitou
                produtoSimulado.setValorTotalDesejado(request.getValorDesejado());

        // Valor total de crédito = soma das amortizações de SAC e PRICE
                BigDecimal valorTotalCredito = sac.getParcelas().stream()
                        .map(ParcelaDto::getValorAmortizacao)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .add(
                                price.getParcelas().stream()
                                        .map(ParcelaDto::getValorAmortizacao)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        );

                produtoSimulado.setValorTotalCredito(valorTotalCredito);

        // Adicionar na lista
                simulacoesRealizadas.add(produtoSimulado);

//


        SimulacaoResumoDto resumo = new SimulacaoResumoDto();
        resumo.setIdSimulacao((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
        resumo.setValorDesejado(request.getValorDesejado());
        resumo.setPrazo(request.getPrazo());
        resumo.setValorTotalParcelas(
                sac.getParcelas().stream()
                        .map(ParcelaDto::getValorPrestacao)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        simulacoesHistorico.add(resumo);

    //

        return response;
    }

    private ResultadoSimulacaoDto calcularSAC(BigDecimal valor, BigDecimal taxaMensal, int prazo) {
        List<ParcelaDto> parcelas = new ArrayList<>();
        BigDecimal amortizacao = valor.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= prazo; i++) {
            BigDecimal saldoDevedor = valor.subtract(amortizacao.multiply(BigDecimal.valueOf(i - 1)));
            BigDecimal juros = saldoDevedor.multiply(taxaMensal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prestacao = amortizacao.add(juros);
            parcelas.add(ParcelaDto.builder()
                    .numero(i)
                    .valorAmortizacao(amortizacao)
                    .valorJuros(juros)
                    .valorPrestacao(prestacao)
                    .build());
        }

        return ResultadoSimulacaoDto.builder()
                .tipo("SAC")
                .parcelas(parcelas)
                .build();
    }

    private ResultadoSimulacaoDto calcularPRICE(BigDecimal valor, BigDecimal taxaMensal, int prazo) {
        List<ParcelaDto> parcelas = new ArrayList<>();

        // Definir precisão e modo de arredondamento
        MathContext mc = new MathContext(15, RoundingMode.HALF_UP);

        // Cálculo do fator PRICE: (1 - (1 + i)^(-n))
        BigDecimal fator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.add(taxaMensal).pow(-prazo, mc)
        );

        // Prestação constante: P = V * i / fator
        BigDecimal prestacao = valor.multiply(taxaMensal, mc)
                .divide(fator, 2, RoundingMode.HALF_UP);

        BigDecimal saldo = valor;
        for (int i = 1; i <= prazo; i++) {
            BigDecimal juros = saldo.multiply(taxaMensal, mc).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = prestacao.subtract(juros, mc).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(amortizacao, mc).setScale(2, RoundingMode.HALF_UP);

            parcelas.add(ParcelaDto.builder()
                    .numero(i)
                    .valorAmortizacao(amortizacao)
                    .valorJuros(juros)
                    .valorPrestacao(prestacao)
                    .build());
        }

        return ResultadoSimulacaoDto.builder()
                .tipo("PRICE")
                .parcelas(parcelas)
                .build();
    }
    public SimulacaoResponseDto simularFallback(SimulacaoRequestDto request, String correlationId) {

        log.warn(String.format("Fallback acionado | correlationId=%s | dados=%s", correlationId, request));

        // TODO: Aqui você poderia salvar a requisição em uma fila para reprocessamento posterior
    
        SimulacaoResponseDto fallback = new SimulacaoResponseDto();
        fallback.setDescricaoProduto("Simulação indisponível no momento. Sua solicitação será reprocessada.");
        fallback.setResultadoSimulacao(Collections.emptyList());
    
        return fallback;
    }

    public List<VolumeSimuladoResponseDto> listarValoresPorProdutoDia() {

        // Agrupar por data (hoje, já que não temos data de cada simulação) e por código do produto
        Map<LocalDate, Map<Integer, List<SimulacaoProdutoDto>>> agrupado =
                simulacoesRealizadas.stream()
                        .collect(Collectors.groupingBy(
                                s -> LocalDate.now(),
                                Collectors.groupingBy(SimulacaoProdutoDto::getCodigoProduto)
                        ));

        List<VolumeSimuladoResponseDto> response = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<Integer, List<SimulacaoProdutoDto>>> entryData : agrupado.entrySet()) {
            LocalDate data = entryData.getKey();
            Map<Integer, List<SimulacaoProdutoDto>> porProduto = entryData.getValue();

            List<SimulacaoProdutoDto> simulacoesPorProduto = new ArrayList<>();

            for (Map.Entry<Integer, List<SimulacaoProdutoDto>> entryProduto : porProduto.entrySet()) {
                Integer codigoProduto = entryProduto.getKey();
                List<SimulacaoProdutoDto> simulacoesProduto = entryProduto.getValue();

                // Agregar valores
                BigDecimal taxaMediaJuro = simulacoesProduto.stream()
                        .map(SimulacaoProdutoDto::getTaxaMediaJuro)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(simulacoesProduto.size()), 9, RoundingMode.HALF_UP);

                BigDecimal valorTotalDesejado = simulacoesProduto.stream()
                        .map(SimulacaoProdutoDto::getValorTotalDesejado)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal valorMedioPrestacao = simulacoesProduto.stream()
                        .map(SimulacaoProdutoDto::getValorMedioPrestacao)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(simulacoesProduto.size()), 2, RoundingMode.HALF_UP);

                BigDecimal valorTotalCredito = simulacoesProduto.stream()
                        .map(SimulacaoProdutoDto::getValorTotalCredito)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                String descricaoProduto = simulacoesProduto.get(0).getDescricaoProduto();

                SimulacaoProdutoDto dto = new SimulacaoProdutoDto();
                dto.setCodigoProduto(codigoProduto);
                dto.setDescricaoProduto(descricaoProduto);
                dto.setTaxaMediaJuro(taxaMediaJuro);
                dto.setValorTotalDesejado(valorTotalDesejado);
                dto.setValorTotalCredito(valorTotalCredito);
                dto.setValorMedioPrestacao(valorMedioPrestacao);

                simulacoesPorProduto.add(dto);
            }

            VolumeSimuladoResponseDto volumeDto = new VolumeSimuladoResponseDto();
            volumeDto.setDataReferencia(data);
            volumeDto.setSimulacoes(simulacoesPorProduto);

            response.add(volumeDto);
        }

        return response;
    }
    public ListaSimulacoesResponseDto listarSimulacoes() {
        ListaSimulacoesResponseDto response = new ListaSimulacoesResponseDto();
        response.setPagina(1);
        response.setQtdRegistros(simulacoesHistorico.size());
        response.setQtdRegistrosPagina(simulacoesHistorico.size());
        response.setRegistros(new ArrayList<>(simulacoesHistorico));
        return response;
    }



}
