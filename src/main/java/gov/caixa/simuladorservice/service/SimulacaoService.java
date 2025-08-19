package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.Produto;
import gov.caixa.simuladorservice.entity.Simulacao;
import gov.caixa.simuladorservice.producer.EventHubProducer;
import gov.caixa.simuladorservice.repository.ProdutoRepository;
import gov.caixa.simuladorservice.repository.SimulacaoRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class SimulacaoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    EventHubProducer eventHubProducer;

    @CacheResult(cacheName = "simulacoes")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 1, delay = 5000)
   // @Fallback(fallbackMethod = "simularFallback")  // TODO: Aqui
    @Transactional
    public SimulacaoResponseDto simular(SimulacaoRequestDto request, String correlationId) {

        long inicio = System.currentTimeMillis();

        // Buscar produto compatível
        Optional<Produto> produtoOpt = buscarProdudoCompativel(request);

        if (produtoOpt.isEmpty()) {
            log.warn("Nenhum produto compatível encontrado...");

            SimulacaoResponseDto erro = new SimulacaoResponseDto();
            erro.setDescricaoProduto("Nenhum produto compatível encontrado.");
            return erro;
        }
        Produto produto = produtoOpt.get();

        // Calcular SAC e PRICE
        ResultadoSimulacaoDto sac = calcularSAC(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());
        ResultadoSimulacaoDto price = calcularPRICE(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());

        // Calcular valor total das parcelas (usando PRICE e sac) //TODO
        BigDecimal valorTotalParcelasSac = sac.getParcelas().stream()
                .map(ParcelaDto::getValorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalParcelasPrice = price.getParcelas().stream()
                .map(ParcelaDto::getValorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Simulacao simulacao = Simulacao.builder()
                .produto(produto.getNoProduto())
                .dataSimulacao(LocalDate.now())
                .valorSimulado(request.getValorDesejado())
                .prazo(request.getPrazo())
                .valorTotalParcelasSac(valorTotalParcelasSac)
                .valorTotalParcelasPrice(valorTotalParcelasPrice)
                .tempoRespostaMs(System.currentTimeMillis() - inicio)
                .build();

        simulacaoRepository.salvar(simulacao);

        SimulacaoResponseDto response = montarRetornoSimulacao(produto, sac, price);

        // Enviar para EventHub
        //TODO eventHubProducer.enviarEvento(response.toString(), correlationId);
        log.info(String.format("Evento enviado para EventHub | correlationId=%s | payload=%s", correlationId, response));

        return response;
    }

    private SimulacaoResponseDto montarRetornoSimulacao(Produto produto, ResultadoSimulacaoDto sac, ResultadoSimulacaoDto price) {
        SimulacaoResponseDto response = new SimulacaoResponseDto();
        response.setCodigoProduto(produto.getCoProduto().longValue());
        response.setDescricaoProduto(produto.getNoProduto());
        response.setTaxaJuros(produto.getPcTaxaJuros());
        response.setResultadoSimulacao(List.of(sac, price));
        return response;
    }

    private Optional<Produto>buscarProdudoCompativel(SimulacaoRequestDto request){
        return  produtoRepository.listarTodos().stream()
                .filter(p -> request.getValorDesejado().compareTo(p.getVrMinimo()) >= 0
                        && (p.getVrMaximo() == null || request.getValorDesejado().compareTo(p.getVrMaximo()) <= 0)
                        && request.getPrazo() >= p.getNuMinimoMeses()
                        && (p.getNuMaximoMeses() == null || request.getPrazo() <= p.getNuMaximoMeses()))
                .findFirst();
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
        List<Simulacao> simulacoes = simulacaoRepository.listarTodas();

        // Agrupar por dataSimulacao e produto
        Map<LocalDate, Map<String, List<Simulacao>>> agrupado = simulacoes.stream()
                .collect(Collectors.groupingBy(
                        Simulacao::getDataSimulacao,
                        Collectors.groupingBy(Simulacao::getProduto)
                ));

        List<VolumeSimuladoResponseDto> response = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<String, List<Simulacao>>> entryData : agrupado.entrySet()) {
            LocalDate data = entryData.getKey();
            Map<String, List<Simulacao>> porProduto = entryData.getValue();

            List<SimulacaoProdutoDto> simulacoesPorProduto = new ArrayList<>();

            for (Map.Entry<String, List<Simulacao>> entryProduto : porProduto.entrySet()) {
                String nomeProduto = entryProduto.getKey();
                List<Simulacao> simulacoesProduto = entryProduto.getValue();

                BigDecimal taxaMediaJuro = BigDecimal.ZERO; // Se quiser calcular, precisaria incluir na entidade
                BigDecimal valorTotalDesejado = simulacoesProduto.stream()
                        .map(Simulacao::getValorSimulado)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal valorMedioPrestacao = BigDecimal.ZERO; // Se quiser calcular, precisaria incluir na entidade
                BigDecimal valorTotalCredito = BigDecimal.ZERO; // idem

                SimulacaoProdutoDto dto = new SimulacaoProdutoDto();
                dto.setCodigoProduto(0); // Se tiver código, incluir na entidade
                dto.setDescricaoProduto(nomeProduto);
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
        List<Simulacao> simulacoes = simulacaoRepository.listarTodas();

        List<SimulacaoResumoDto> registros = simulacoes.stream().map(simulacao -> {
            SimulacaoResumoDto dto = new SimulacaoResumoDto();
            dto.setIdSimulacao(simulacao.getId().intValue());
            dto.setValorDesejado(simulacao.getValorSimulado());
            dto.setPrazo(simulacao.getPrazo());
            dto.setValorTotalParcelas(simulacao.getValorTotalParcelas());
            return dto;
        }).collect(Collectors.toList());

        ListaSimulacoesResponseDto response = new ListaSimulacoesResponseDto();
        response.setPagina(1);
        response.setQtdRegistros(registros.size());
        response.setQtdRegistrosPagina(registros.size());
        response.setRegistros(registros);

        return response;
    }

}
