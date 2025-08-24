package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.EndpointTelemetriaDto;
import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class TelemetriaMapper {

    public TelemetriaEntity mapDtoParaEntity(EndpointTelemetriaDto dto, LocalDate data) {
        TelemetriaEntity entity = TelemetriaEntity.builder()
                .nomeApi(mapPathToNomeApi(dto.getNomeApi()))
                .qtdRequisicoes(dto.getQtdRequisicoes())
                .tempoMedio(dto.getTempoMedio())
                .tempoMinimo(dto.getTempoMinimo())
                .tempoMaximo(dto.getTempoMaximo())
                .percentualSucesso(dto.getPercentualSucesso())
                .data(data)
                .build();
        return entity;
    }

    private String mapPathToNomeApi(String path) {
        return switch (path) {
            case "/api/v1/simulacoes" -> "Simulacao";
            case "/api/v1/simulacoes/listar" -> "ListarSimulacoes";
            case "/api/v1/simulacoes/valores-por-produto-dia" -> "VolumeSimulado";
            case "/api/v1/telemetria/listar" -> "Telemetria";
            default -> path;
        };
    }
}

