package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.ParcelaDto;
import gov.caixa.simuladorservice.dto.ResultadoSimulacaoDto;
import gov.caixa.simuladorservice.dto.SimulacaoRequestDto;
import gov.caixa.simuladorservice.dto.SimulacaoResponseDto;
import gov.caixa.simuladorservice.entity.Produto;
import gov.caixa.simuladorservice.producer.EventHubProducer;
import gov.caixa.simuladorservice.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class SimulacaoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    EventHubProducer eventHubProducer;

    public SimulacaoResponseDto simular(SimulacaoRequestDto request) {
        // Validar entrada
        if (request.getValorDesejado() == null || request.getPrazo() == null || request.getPrazo() <= 0) {
            throw new IllegalArgumentException("Parâmetros inválidos: valorDesejado e prazo são obrigatórios.");
        }

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
        eventHubProducer.enviarEvento(response.toString());
        log.info("Simulação enviada para EventHub: {}", response);

        return response;
    }

    private ResultadoSimulacaoDto calcularSAC(BigDecimal valor, BigDecimal taxa, int prazo) {
        List<ParcelaDto> parcelas = new ArrayList<>();
        BigDecimal amortizacao = valor.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= prazo; i++) {
            BigDecimal saldoDevedor = valor.subtract(amortizacao.multiply(BigDecimal.valueOf(i - 1)));
            BigDecimal juros = saldoDevedor.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
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

    private ResultadoSimulacaoDto calcularPRICE(BigDecimal valor, BigDecimal taxa, int prazo) {
        List<ParcelaDto> parcelas = new ArrayList<>();

        // Definir precisão e modo de arredondamento
        MathContext mc = new MathContext(15, RoundingMode.HALF_UP);

        BigDecimal taxaMensal = taxa;

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

}
