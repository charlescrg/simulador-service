package gov.caixa.simuladorservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO que representa o valor total das parcelas por tipo")
public class ValorTotalParcelasTipoDto {
    @Schema(description = "Tipo da parcela", example = "FINANCIAMENTO")
    private String tipo;

    @Schema(description = "Valor total das parcelas", example = "15000.00")
    private BigDecimal valorTotalParcelas;
}
