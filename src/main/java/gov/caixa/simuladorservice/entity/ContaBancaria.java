package gov.caixa.simuladorservice.entity;

import gov.caixa.simuladorservice.enums.StatusConta;
import gov.caixa.simuladorservice.enums.TipoConta;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas_bancarias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informações da conta bancária")
public class ContaBancaria extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da conta", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @Schema(description = "Cliente dono da conta")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "agencia_id", nullable = false)
    @Schema(description = "Agência da conta")
    private Agencia agencia;

    @Schema(description = "Número da conta", example = "123456-7")
    @Column(nullable = false, unique = true)
    private String numeroConta;

    @Schema(description = "Dígito verificador da conta", example = "7")
    @Column(nullable = false)
    private String digito;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo da conta")
    @Column(nullable = false)
    private TipoConta tipoConta;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status atual da conta")
    @Column(nullable = false)
    private StatusConta statusConta;

    @Schema(description = "Saldo atual da conta", example = "1500.50")
    @Column(nullable = false)
    private BigDecimal saldo;

    @Schema(description = "Data de abertura da conta")
    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    @Schema(description = "Data de encerramento (caso encerrada)")
    private LocalDateTime dataEncerramento;
}


