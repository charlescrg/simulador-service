package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.TelemetriaRequestDto;
import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TelemetriaMapperTest {

    private final TelemetriaMapper mapper = new TelemetriaMapper();

    @Test
    void testMapDtoParaEntity() {
        TelemetriaRequestDto dto = new TelemetriaRequestDto();
        dto.setNomeApi("/api/v1/simulacoes");
        dto.setTempoMs(50);
        dto.setSucesso(1);

        LocalDate data = LocalDate.of(2025, 8, 24);

        TelemetriaEntity entity = mapper.mapDtoParaEntity(dto, data);

        assertNotNull(entity);
        assertEquals("/api/v1/simulacoes", entity.getNomeApi());
        assertEquals(50, entity.getTempoMs());
        assertEquals(1, entity.getSucesso());
        assertEquals(data, entity.getData());
    }

    @Test
    void testMapDtoParaEntityComSucessoNull() {
        TelemetriaRequestDto dto = new TelemetriaRequestDto();
        dto.setNomeApi("/api/v1/simulacoes");
        dto.setTempoMs(120);
        dto.setSucesso(null);

        LocalDate data = LocalDate.of(2025, 8, 25);

        TelemetriaEntity entity = mapper.mapDtoParaEntity(dto, data);

        assertNotNull(entity);
        assertEquals("/api/v1/simulacoes", entity.getNomeApi());
        assertEquals(120, entity.getTempoMs());
        assertEquals(0, entity.getSucesso()); // sucesso null vira 0
        assertEquals(data, entity.getData());
    }
}
