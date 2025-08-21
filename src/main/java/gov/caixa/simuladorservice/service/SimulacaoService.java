package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.*;
import gov.caixa.simuladorservice.entity.produto.ProdutoExternoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoEntity;
import gov.caixa.simuladorservice.entity.simulacao.SimulacaoTipoEntity;
import gov.caixa.simuladorservice.exception.SimulacaoIndisponivelException;
import gov.caixa.simuladorservice.mapper.SimulacaoMapper;
import gov.caixa.simuladorservice.repository.ProdutoExternoRepository;
import gov.caixa.simuladorservice.repository.SimulacaoRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class SimulacaoService {

    @Inject
    @PersistenceUnit(unitName = "local")
    SimulacaoRepository simulacaoRepository;

    @Inject
    SimuladorFinanceiroService simuladorFinanceiroService;

    @Inject
    SimulacaoMapper simulacaoMapper;

    @Inject
    @PersistenceUnit(unitName = "external")
    ProdutoExternoRepository produtoExternoRepository;


    @CacheResult(cacheName = "simulacoes")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 1, delay = 5000)
    @Fallback(fallbackMethod = "simularFallback")
    public SimulacaoResponseDto simular(SimulacaoRequestDto request, String correlationId) {
        long inicio = System.currentTimeMillis();

        ProdutoExternoEntity produto = buscarProduto(request);
        if (produto == null) {
            log.warn("Nenhum produto compatível encontrado...");
            return SimulacaoResponseDto.builder()
                    .descricaoProduto("Nenhum produto compatível encontrado.")
                    .build();
        }

        ResultadoSimulacaoDto sac = simuladorFinanceiroService.calcularSAC(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());
        ResultadoSimulacaoDto price = simuladorFinanceiroService.calcularPRICE(request.getValorDesejado(), produto.getPcTaxaJuros(), request.getPrazo());

        SimulacaoEntity simulacao = simulacaoMapper.criarSimulacao(request, produto, System.currentTimeMillis() - inicio);

        List<SimulacaoTipoEntity> tipos = criarTiposSimulacao(simulacao, produto, sac, price, request);
        simulacao.setTipos(tipos);

        salvarSimulacao(simulacao);

        SimulacaoResponseDto response = simulacaoMapper.montarRetornoSimulacao(produto, sac, price, simulacao);

        return response;
    }

    @Transactional
    public void salvarSimulacao(SimulacaoEntity simulacao) {
        simulacaoRepository.salvar(simulacao);
    }

    @CacheResult(cacheName = "listarValoresPorProdutoDia")
    public List<VolumeSimuladoResponseDto> listarValoresPorProdutoDia() {
        List<SimulacaoEntity> simulacoes = simulacaoRepository.listarTodas();

        Map<LocalDate, Map<String, List<SimulacaoEntity>>> agrupado = simulacaoMapper.agruparPorDataEProduto(simulacoes);

        List<VolumeSimuladoResponseDto> response = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<String, List<SimulacaoEntity>>> entryData : agrupado.entrySet()) {
            LocalDate data = entryData.getKey();
            Map<String, List<SimulacaoEntity>> porProduto = entryData.getValue();

            List<SimulacaoProdutoDto> simulacoesPorProduto = porProduto.entrySet().stream()
                    .map(entry -> {
                        List<SimulacaoTipoEntity> tiposSAC = simulacaoMapper.filtrarTipos(entry.getValue(), "SAC");
                        List<SimulacaoTipoEntity> tiposPRICE = simulacaoMapper.filtrarTipos(entry.getValue(), "PRICE");
                        BigDecimal taxaMediaJuroSAC = simuladorFinanceiroService.calcularMedia(tiposSAC.stream().map(SimulacaoTipoEntity::getTaxaJuro).toList(), 3);
                        BigDecimal taxaMediaJuroPRICE = simuladorFinanceiroService.calcularMedia(tiposPRICE.stream().map(SimulacaoTipoEntity::getTaxaJuro).toList(), 3);
                        BigDecimal valorMedioPrestacaoSAC = simuladorFinanceiroService.calcularMedia(tiposSAC.stream().map(SimulacaoTipoEntity::getValorMedioPrestacao).toList(), 2);
                        BigDecimal valorMedioPrestacaoPRICE = simuladorFinanceiroService.calcularMedia(tiposPRICE.stream().map(SimulacaoTipoEntity::getValorMedioPrestacao).toList(), 2);
                        BigDecimal valorTotalCreditoSAC = simuladorFinanceiroService.somarValores(tiposSAC.stream().map(SimulacaoTipoEntity::getValorTotalParcelas).toList());
                        BigDecimal valorTotalCreditoPRICE = simuladorFinanceiroService.somarValores(tiposPRICE.stream().map(SimulacaoTipoEntity::getValorTotalParcelas).toList());
                        BigDecimal valorTotalDesejado = simuladorFinanceiroService.somarValores(entry.getValue().stream().map(SimulacaoEntity::getValorSimulado).toList());
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
        List<SimulacaoEntity> simulacoes = simulacaoRepository.listarTodas();

        List<SimulacaoResumoDto> registros = simulacoes.stream()
                .map(simulacaoMapper::mapearParaResumo)
                .collect(Collectors.toList());
        return simulacaoMapper.montarListaSimulacoes(registros);
    }
    @Transactional
    public ProdutoExternoEntity buscarProduto(SimulacaoRequestDto request) {
        return buscarProdudoCompativel(request).orElse(null);
    }

    public SimulacaoResponseDto simularFallback(SimulacaoRequestDto request, String correlationId) {
        throw new SimulacaoIndisponivelException("Banco indisponível");
    }

    private Optional<ProdutoExternoEntity> buscarProdudoCompativel(SimulacaoRequestDto request) {
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

        BigDecimal totalSac = simuladorFinanceiroService.calcularTotalPrestacoes(sac.getParcelas());
        BigDecimal mediaSac = simuladorFinanceiroService.calcularMediaPrestacao(sac.getParcelas());
        BigDecimal totalPrice = simuladorFinanceiroService.calcularTotalPrestacoes(price.getParcelas());
        BigDecimal mediaPrice = simuladorFinanceiroService.calcularMediaPrestacao(price.getParcelas());

        return List.of(
                new SimulacaoTipoEntity(null, "SAC", totalSac, simulacao, mediaSac, produto.getPcTaxaJuros(), BigDecimal.valueOf(request.getPrazo())),
                new SimulacaoTipoEntity(null, "PRICE", totalPrice, simulacao, mediaPrice, produto.getPcTaxaJuros(), BigDecimal.valueOf(request.getPrazo()))
        );
    }
}
