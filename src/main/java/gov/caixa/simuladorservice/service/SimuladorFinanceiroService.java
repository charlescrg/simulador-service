package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.ParcelaDto;
import gov.caixa.simuladorservice.dto.ResultadoSimulacaoDto;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimuladorFinanceiroService {

    public ResultadoSimulacaoDto calcularSAC(BigDecimal valor, BigDecimal taxaMensal, int prazo) {
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

    public ResultadoSimulacaoDto calcularPRICE(BigDecimal valor, BigDecimal taxaMensal, int prazo) {
        List<ParcelaDto> parcelas = new ArrayList<>();
        MathContext mc = new MathContext(15, RoundingMode.HALF_UP);
        BigDecimal fator = BigDecimal.ONE.subtract(
                BigDecimal.ONE.add(taxaMensal).pow(-prazo, mc)
        );
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

    public BigDecimal calcularMediaPrestacao(List<ParcelaDto> parcelas) {
        BigDecimal soma = parcelas.stream()
                .map(ParcelaDto::getValorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return parcelas.isEmpty() ? BigDecimal.ZERO :
                soma.divide(BigDecimal.valueOf(parcelas.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotalPrestacoes(List<ParcelaDto> parcelas) {
        return parcelas.stream()
                .map(ParcelaDto::getValorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularMedia(List<BigDecimal> valores, int escala) {
        if (valores.isEmpty()) return BigDecimal.ZERO;

        BigDecimal soma = valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return soma.divide(BigDecimal.valueOf(valores.size()), escala, RoundingMode.HALF_UP);
    }

    public BigDecimal somarValores(List<BigDecimal> valores) {
        return valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
