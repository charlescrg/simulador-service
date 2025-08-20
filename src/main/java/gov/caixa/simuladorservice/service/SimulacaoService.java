package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.Produto;
import gov.caixa.simuladorservice.entity.Simulacao;
import gov.caixa.simuladorservice.entity.SimulacaoTipo;
import gov.caixa.simuladorservice.mapper.SimulacaoMapper;
import gov.caixa.simuladorservice.producer.EventHubProducer;
import gov.caixa.simuladorservice.repository.ProdutoRepository;
import gov.caixa.simuladorservice.repository.SimulacaoRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;

import java.math.BigDecimal;
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

    @Inject
    SimuladorFinanceiroService simuladorFinanceiroService;

    @Inject
    SimulacaoMapper simulacaoMapper;

    @CacheResult(cacheName = "simulacoes")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 1, delay = 5000)
    @Fallback(fallbackMethod = "simularFallback")
    @Transactional
    public SimulacaoResponseDto simular(SimulacaoRequestDto request, String correlationId) {
        long inicio = System.currentTimeMillis();

        Produto produto = buscarProduto(request);
        if (produto == null) {
            log.warn("Nenhum produto compatível encontrado...");
            return SimulacaoResponseDto.builder()
                    .descricaoProduto("Nenhum produto compatível encontrado.")
                    .build();
        }

        ResultadoSimulacaoDto sac = simuladorFinanceiroService.calcularSAC(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());
        ResultadoSimulacaoDto price = simuladorFinanceiroService.calcularPRICE(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());

        Simulacao simulacao = simulacaoMapper.criarSimulacao(request, produto, System.currentTimeMillis() - inicio);

        List<SimulacaoTipo> tipos = criarTiposSimulacao(simulacao, produto, sac, price, request);
        simulacao.setTipos(tipos);

        simulacaoRepository.salvar(simulacao);

        SimulacaoResponseDto response = simulacaoMapper.montarRetornoSimulacao(produto, sac, price, simulacao);

        return response;
    }

    @CacheResult(cacheName = "listarValoresPorProdutoDia")
    public List<VolumeSimuladoResponseDto> listarValoresPorProdutoDia() {
        List<Simulacao> simulacoes = simulacaoRepository.listarTodas();

        Map<LocalDate, Map<String, List<Simulacao>>> agrupado = simulacaoMapper.agruparPorDataEProduto(simulacoes);

        List<VolumeSimuladoResponseDto> response = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<String, List<Simulacao>>> entryData : agrupado.entrySet()) {
            LocalDate data = entryData.getKey();
            Map<String, List<Simulacao>> porProduto = entryData.getValue();

            List<SimulacaoProdutoDto> simulacoesPorProduto = porProduto.entrySet().stream()
                    .map(entry -> {
                        List<SimulacaoTipo> tiposSAC = simulacaoMapper.filtrarTipos(entry.getValue(), "SAC");
                        List<SimulacaoTipo> tiposPRICE = simulacaoMapper.filtrarTipos(entry.getValue(), "PRICE");
                        BigDecimal taxaMediaJuroSAC = simuladorFinanceiroService.calcularMedia(tiposSAC.stream().map(SimulacaoTipo::getTaxaJuro).toList(), 3);
                        BigDecimal taxaMediaJuroPRICE = simuladorFinanceiroService.calcularMedia(tiposPRICE.stream().map(SimulacaoTipo::getTaxaJuro).toList(), 3);
                        BigDecimal valorMedioPrestacaoSAC = simuladorFinanceiroService.calcularMedia(tiposSAC.stream().map(SimulacaoTipo::getValorMedioPrestacao).toList(), 2);
                        BigDecimal valorMedioPrestacaoPRICE = simuladorFinanceiroService.calcularMedia(tiposPRICE.stream().map(SimulacaoTipo::getValorMedioPrestacao).toList(), 2);
                        BigDecimal valorTotalCreditoSAC = simuladorFinanceiroService.somarValores(tiposSAC.stream().map(SimulacaoTipo::getValorTotalParcelas).toList());
                        BigDecimal valorTotalCreditoPRICE = simuladorFinanceiroService.somarValores(tiposPRICE.stream().map(SimulacaoTipo::getValorTotalParcelas).toList());
                        BigDecimal valorTotalDesejado = simuladorFinanceiroService.somarValores(entry.getValue().stream().map(Simulacao::getValorSimulado).toList());
                        Integer codigoProduto = entry.getValue().isEmpty() ? 0 : entry.getValue().get(0).getCodigoProduto();

                        return simulacaoMapper.montarDtoProduto(entry.getKey(), entry.getValue(), tiposSAC, tiposPRICE,
                                taxaMediaJuroSAC, taxaMediaJuroPRICE,
                                valorMedioPrestacaoSAC, valorMedioPrestacaoPRICE,
                                valorTotalCreditoSAC, valorTotalCreditoPRICE,
                                valorTotalDesejado, codigoProduto);
                    })
                    .toList();

            response.add(VolumeSimuladoResponseDto.builder()
                    .dataReferencia(data)
                    .simulacoes(simulacoesPorProduto)
                    .build());
        }
        return response;
    }

    @CacheResult(cacheName = "listarSimulacoes")
    public ListaSimulacoesResponseDto listarSimulacoes() {
        List<Simulacao> simulacoes = simulacaoRepository.listarTodas();

        List<SimulacaoResumoDto> registros = simulacoes.stream()
                .map(simulacaoMapper::mapearParaResumo)
                .collect(Collectors.toList());
        return simulacaoMapper.montarListaSimulacoes(registros);
    }

    private Produto buscarProduto(SimulacaoRequestDto request) {
        return buscarProdudoCompativel(request).orElse(null);
    }

    public SimulacaoResponseDto simularFallback(SimulacaoRequestDto request, String correlationId) {
        log.warn(String.format("Fallback acionado | correlationId=%s | dados=%s", correlationId, request));
        // TODO: Aqui você poderia salvar a requisição em uma fila para reprocessamento posterior

        SimulacaoResponseDto fallback = new SimulacaoResponseDto();
        fallback.setDescricaoProduto("Simulação indisponível no momento. Sua solicitação será reprocessada.");
        fallback.setResultadoSimulacao(Collections.emptyList());
        return fallback;
    }

    private Optional<Produto>buscarProdudoCompativel(SimulacaoRequestDto request){
        return  produtoRepository.listarTodos().stream()
                .filter(p -> request.getValorDesejado().compareTo(p.getVrMinimo()) >= 0
                        && (p.getVrMaximo() == null || request.getValorDesejado().compareTo(p.getVrMaximo()) <= 0)
                        && request.getPrazo() >= p.getNuMinimoMeses()
                        && (p.getNuMaximoMeses() == null || request.getPrazo() <= p.getNuMaximoMeses()))
                .findFirst();
    }

    private List<SimulacaoTipo> criarTiposSimulacao(Simulacao simulacao, Produto produto,
                                                    ResultadoSimulacaoDto sac, ResultadoSimulacaoDto price,
                                                    SimulacaoRequestDto request) {

        BigDecimal totalSac = simuladorFinanceiroService.calcularTotalPrestacoes(sac.getParcelas());
        BigDecimal mediaSac = simuladorFinanceiroService.calcularMediaPrestacao(sac.getParcelas());
        BigDecimal totalPrice = simuladorFinanceiroService.calcularTotalPrestacoes(price.getParcelas());
        BigDecimal mediaPrice = simuladorFinanceiroService.calcularMediaPrestacao(price.getParcelas());

        return List.of(
                new SimulacaoTipo(null, "SAC", totalSac, simulacao, mediaSac, produto.getPcTaxaJuros(), BigDecimal.valueOf(request.getPrazo())),
                new SimulacaoTipo(null, "PRICE", totalPrice, simulacao, mediaPrice, produto.getPcTaxaJuros(), BigDecimal.valueOf(request.getPrazo()))
        );
    }
}
