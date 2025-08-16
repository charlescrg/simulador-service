package gov.caixa.simuladorservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SimulacaoRequest", description = "Dados da requisição de simulação")
public class SimulacaoRequestDto {

    @NotNull(message = "O valor desejado é obrigatório")
    @DecimalMin(value = "100.00", message = "O valor mínimo permitido é R$100,00")
    @DecimalMax(value = "1000000.00", message = "O valor máximo permitido é R$1.000.000,00")
    @Schema(description = "Valor desejado para o empréstimo", example = "10000")
    private BigDecimal valorDesejado;

    @NotNull(message = "O prazo é obrigatório")
    @Min(value = 1, message = "O prazo mínimo é 1 mês")
    @Max(value = 120, message = "O prazo máximo é 120 meses")
    @Schema(description = "Prazo em meses para pagamento", example = "24")
    private Integer prazo;
}
