package gov.caixa.simuladorservice.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValorTotalParcelasTipoDto {
    private String tipo;
    private BigDecimal valorTotalParcelas;
}

