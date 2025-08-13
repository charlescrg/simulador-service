package gov.caixa.simuladorservice.dto;

import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Builder
@Schema(name = "Parcela", description = "Detalhes de cada parcela do empréstimo")
public class ParcelaDto {

    @Schema(description = "Número da parcela", example = "1")
    private Integer numero;

    @Schema(description = "Valor da amortização", example = "416.67")
    private BigDecimal valorAmortizacao;

    @Schema(description = "Valor dos juros", example = "50.00")
    private BigDecimal valorJuros;

    @Schema(description = "Valor total da prestação", example = "466.67")
    private BigDecimal valorPrestacao;
}
