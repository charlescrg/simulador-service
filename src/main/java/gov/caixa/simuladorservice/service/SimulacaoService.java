package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.config.TracingConfig;
import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoTipoEntity;
import gov.caixa.simuladorservice.exception.ProdutoNaoEncontradoException;
import gov.caixa.simuladorservice.mapper.SimulacaoMapper;
import gov.caixa.simuladorservice.repository.ProdutoExternoRepository;
import gov.caixa.simuladorservice.repository.SimulacaoRepository;
import gov.caixa.simuladorservice.util.SimulacaoUtils;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class SimulacaoService {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    SimuladorFinanceiroService simuladorFinanceiroService;

    @Inject
    SimulacaoMapper simulacaoMapper;

    @Inject
    ProdutoExternoRepository produtoExternoRepository;

    @Inject
    EventService eventService;

    @CacheResult(cacheName = "simulacoes")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 1, delay = 5000)
    public SimulacaoResponseDto simular(SimulacaoRequestDto request, String correlationId) {
        Tracer tracer = TracingConfig.getTracer();
        Span span = tracer.spanBuilder("simular").startSpan();
        long inicio = System.currentTimeMillis();

        try (var scope = span.makeCurrent()) {
            ProdutoExternoEntity produto = buscarProduto(request)
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("Nenhum produto compatível encontrado."));

            ResultadoSimulacaoDto sac = simuladorFinanceiroService.calcularSAC(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());
            ResultadoSimulacaoDto price = simuladorFinanceiroService.calcularPRICE(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());

            SimulacaoEntity simulacao = simulacaoMapper.criarSimulacao(request, produto, System.currentTimeMillis() - inicio);
            simulacao.setTipos(criarTiposSimulacao(simulacao, produto, sac, price, request));

            salvarSimulacao(simulacao);

            SimulacaoResponseDto response = simulacaoMapper.montarRetornoSimulacao(produto, sac, price, simulacao);
            enviarEvento(String.valueOf(response), correlationId);
            return response;
        } finally {
            span.end();
        }
    }

    @Transactional
    public void salvarSimulacao(SimulacaoEntity simulacao) {
        simulacaoRepository.salvar(simulacao);
    }

    public Optional<ProdutoExternoEntity> buscarProduto(SimulacaoRequestDto request) {
        return produtoExternoRepository.listarTodos().stream()
                .filter(p -> request.getValorDesejado().compareTo(p.getVrMinimo()) >= 0
                        && (p.getVrMaximo() == null || request.getValorDesejado().compareTo(p.getVrMaximo()) <= 0)
                        && request.getPrazo() >= p.getNuMinimoMeses()
                        && (p.getNuMaximoMeses() == null || request.getPrazo() <= p.getNuMaximoMeses()))
                .findFirst();
    }

    private List<SimulacaoTipoEntity> criarTiposSimulacao(SimulacaoEntity simulacao, ProdutoExternoEntity produto,
                                                          ResultadoSimulacaoDto sac, ResultadoSimulacaoDto price,
                                                          SimulacaoRequestDto request) {
        BigDecimal totalSac = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularTotalPrestacoes(sac.getParcelas()), 2);
        BigDecimal mediaSac = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularMediaPrestacao(sac.getParcelas()), 2);
        BigDecimal totalPrice = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularTotalPrestacoes(price.getParcelas()), 2);
        BigDecimal mediaPrice = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularMediaPrestacao(price.getParcelas()), 2);

        return List.of(
                new SimulacaoTipoEntity(null, "SAC", totalSac, simulacao, mediaSac, produto.getPcTaxaJuros(), BigDecimal.valueOf(request.getPrazo())),
                new SimulacaoTipoEntity(null, "PRICE", totalPrice, simulacao, mediaPrice, produto.getPcTaxaJuros(), BigDecimal.valueOf(request.getPrazo()))
        );
    }

    public ListaSimulacoesResponseDto listarSimulacoes() {
        List<SimulacaoEntity> simulacoes = simulacaoRepository.listarTodas();
        List<SimulacaoResumoDto> registros = simulacoes.stream()
                .map(simulacaoMapper::mapearParaResumo)
                .collect(Collectors.toList());
        return new ListaSimulacoesResponseDto(1, registros.size(), registros.size(), registros);
    }

    public List<VolumeSimuladoResponseDto> listarValoresPorProdutoDia() {
        List<SimulacaoEntity> simulacoes = simulacaoRepository.listarTodas();
        Map<LocalDate, Map<String, List<SimulacaoEntity>>> agrupado = SimulacaoUtils.agruparPorDataEProduto(simulacoes);

        return agrupado.entrySet().stream()
                .map(entryData -> {
                    LocalDate data = entryData.getKey();
                    List<SimulacaoProdutoDto> simulacoesPorProduto = entryData.getValue().entrySet().stream()
                            .map(entry -> {
                                List<SimulacaoTipoEntity> tiposSAC = SimulacaoUtils.filtrarTipos(entry.getValue(), "SAC");
                                List<SimulacaoTipoEntity> tiposPRICE = SimulacaoUtils.filtrarTipos(entry.getValue(), "PRICE");

                                BigDecimal taxaMediaJuroSAC = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularMedia(tiposSAC.stream().map(SimulacaoTipoEntity::getTaxaJuro).toList(), 3), 2);
                                BigDecimal taxaMediaJuroPRICE = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularMedia(tiposPRICE.stream().map(SimulacaoTipoEntity::getTaxaJuro).toList(), 3), 2);
                                BigDecimal valorMedioPrestacaoSAC = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularMedia(tiposSAC.stream().map(SimulacaoTipoEntity::getValorMedioPrestacao).toList(), 2), 2);
                                BigDecimal valorMedioPrestacaoPRICE = SimulacaoUtils.arredondar(simuladorFinanceiroService.calcularMedia(tiposPRICE.stream().map(SimulacaoTipoEntity::getValorMedioPrestacao).toList(), 2), 2);
                                BigDecimal valorTotalCreditoSAC = SimulacaoUtils.arredondar(simuladorFinanceiroService.somarValores(tiposSAC.stream().map(SimulacaoTipoEntity::getValorTotalParcelas).toList()), 2);
                                BigDecimal valorTotalCreditoPRICE = SimulacaoUtils.arredondar(simuladorFinanceiroService.somarValores(tiposPRICE.stream().map(SimulacaoTipoEntity::getValorTotalParcelas).toList()), 2);
                                BigDecimal valorTotalDesejado = SimulacaoUtils.arredondar(simuladorFinanceiroService.somarValores(entry.getValue().stream().map(SimulacaoEntity::getValorSimulado).toList()), 2);

                                Integer codigoProduto = entry.getValue().isEmpty() ? 0 : entry.getValue().get(0).getCodigoProduto();

                                return SimulacaoUtils.montarDtoProduto(entry.getKey().toString(), entry.getValue(),
                                        taxaMediaJuroSAC, taxaMediaJuroPRICE,
                                        valorMedioPrestacaoSAC, valorMedioPrestacaoPRICE,
                                        valorTotalCreditoSAC, valorTotalCreditoPRICE,
                                        valorTotalDesejado, codigoProduto);
                            })
                            .toList();
                    return VolumeSimuladoResponseDto.builder()
                            .dataReferencia(data)
                            .simulacoes(simulacoesPorProduto)
                            .build();
                })
                .toList();
    }

    private void enviarEvento(String mensagemJson, String correlationId) {
        CompletableFuture.runAsync(() -> {
            try {
                eventService.enviarEvento(mensagemJson, correlationId);
            } catch (Exception e) {
                log.warn("Falha ao enviar evento de simulação (não afeta resposta ao cliente): {}", e.getMessage());
            }
        });
    }
}
