package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.TelemetriaRequestDto;
import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;

@ApplicationScoped
public class TelemetriaMapper {

    public TelemetriaEntity mapDtoParaEntity(TelemetriaRequestDto dto, LocalDate data) {
        TelemetriaEntity entity = new TelemetriaEntity();
        entity.setNomeApi(mapPathToNomeApi(dto.getNomeApi()));
        entity.setTempoMs(dto.getTempoMs());
        entity.setSucesso(dto.getSucesso() != null && dto.getSucesso() > 0 ? 1 : 0);
        entity.setData(data != null ? data : LocalDate.now());
        return entity;
    }


    public String mapPathToNomeApi(String path) {
        return switch (path) {
            case "/api/v1/simulacoes" -> "Simulacao";
            case "/api/v1/simulacoes/listar" -> "ListarSimulacoes";
            case "/api/v1/simulacoes/valores-por-produto-dia" -> "VolumeSimulado";
            case "/api/v1/telemetria/listar" -> "Telemetria";
            default -> path;
        };
    }
}
