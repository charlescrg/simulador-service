package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.EndpointTelemetriaDto;
import gov.caixa.simuladorservice.dto.TelemetriaRequestDto;
import gov.caixa.simuladorservice.dto.TelemetriaResponseDto;
import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import gov.caixa.simuladorservice.mapper.TelemetriaMapper;
import gov.caixa.simuladorservice.repository.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository repository;

    @Inject
    TelemetriaMapper mapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public List<TelemetriaResponseDto> listarMetricas() {
        List<Object[]> resultados = repository.listar(); // Query agregada no banco

        Map<LocalDate, List<EndpointTelemetriaDto>> mapPorData = new LinkedHashMap<>();

        for (Object[] r : resultados) {
            LocalDate data = (LocalDate) r[0];
            String nomeApi = (String) r[1];
            long qtdRequisicoes = ((Number) r[2]).longValue();
            double tempoMedio = arredondar(((Number) r[3]).doubleValue(), 2);
            long tempoMinimo = ((Number) r[4]).longValue();
            long tempoMaximo = ((Number) r[5]).longValue();
            double percentualSucesso = ((Number) r[6]).doubleValue();

            EndpointTelemetriaDto dto = new EndpointTelemetriaDto(
                    nomeApi,
                    qtdRequisicoes,
                    tempoMedio,
                    tempoMinimo,
                    tempoMaximo,
                    percentualSucesso
            );

            mapPorData.computeIfAbsent(data, k -> new ArrayList<>()).add(dto);
        }

        List<TelemetriaResponseDto> metricas = new ArrayList<>();
        for (Map.Entry<LocalDate, List<EndpointTelemetriaDto>> entry : mapPorData.entrySet()) {
            metricas.add(new TelemetriaResponseDto(entry.getKey(), entry.getValue()));
        }

        return metricas;
    }

    public void salvarMetricas(String path, long tempoMs, boolean sucesso) {
        executor.execute(() -> salvar(path, tempoMs, sucesso));
    }

    @Transactional
    protected void salvar(String path, long tempoMs, boolean sucesso) {
        TelemetriaRequestDto dto = new TelemetriaRequestDto(path, tempoMs, sucesso ? 1 : 0);
        TelemetriaEntity entity = mapper.toEntity(dto, LocalDate.now());
        repository.persist(entity);
    }
    private double arredondar(double valor, int casas) {
        return BigDecimal.valueOf(valor)
                .setScale(casas, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
