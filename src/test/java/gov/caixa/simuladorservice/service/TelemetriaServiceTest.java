package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.EndpointTelemetriaDto;
import gov.caixa.simuladorservice.dto.TelemetriaRequestDto;
import gov.caixa.simuladorservice.dto.TelemetriaResponseDto;
import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import gov.caixa.simuladorservice.mapper.TelemetriaMapper;
import gov.caixa.simuladorservice.repository.TelemetriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelemetriaServiceTest {

    private TelemetriaRepository repository;
    private TelemetriaMapper mapper;
    private TelemetriaService service;

    @BeforeEach
    void setUp() {
        repository = mock(TelemetriaRepository.class);
        mapper = mock(TelemetriaMapper.class);
        service = new TelemetriaService();
        service.repository = repository;
        service.mapper = mapper;
    }

    @Test
    void testSalvar() {
        String path = "/api/v1/simulacoes";
        long tempoMs = 120;
        boolean sucesso = true;

        TelemetriaEntity entityMock = new TelemetriaEntity();
        when(mapper.toEntity(any(TelemetriaRequestDto.class), any(LocalDate.class)))
                .thenReturn(entityMock);

        service.salvar(path, tempoMs, sucesso);

        ArgumentCaptor<TelemetriaRequestDto> dtoCaptor = ArgumentCaptor.forClass(TelemetriaRequestDto.class);
        verify(mapper).toEntity(dtoCaptor.capture(), any(LocalDate.class));
        TelemetriaRequestDto dto = dtoCaptor.getValue();

        assertEquals(path, dto.getNomeApi());
        assertEquals(tempoMs, dto.getTempoMs());
        assertEquals(1, dto.getSucesso()); // sucesso true vira 1

        verify(repository).persist(entityMock);
    }

    @Test
    void testListarMetricas() {
        Object[] row1 = { LocalDate.of(2025, 8, 25), "Simulacao", 10L, 50.0, 30L, 70L, 100.0 };
        Object[] row2 = { LocalDate.of(2025, 8, 25), "ListarSimulacoes", 5L, 20.0, 15L, 25L, 80.0 };

        when(repository.listar()).thenReturn(Arrays.asList(row1, row2));

        List<TelemetriaResponseDto> result = service.listarMetricas();

        assertEquals(1, result.size()); // uma data
        TelemetriaResponseDto response = result.get(0);
        assertEquals(LocalDate.of(2025, 8, 25), response.getDataReferencia());
        assertEquals(2, response.getListaEndpoints().size());

        EndpointTelemetriaDto dto1 = response.getListaEndpoints().get(0);
        assertEquals("Simulacao", dto1.getNomeApi());
        assertEquals(10L, dto1.getQtdRequisicoes());
        assertEquals(50.0, dto1.getTempoMedio());
        assertEquals(30L, dto1.getTempoMinimo());
        assertEquals(70L, dto1.getTempoMaximo());
        assertEquals(100.0, dto1.getPercentualSucesso());
    }
}
