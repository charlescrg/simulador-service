package gov.caixa.simuladorservice.service;

import gov.caixa.simuladorservice.dto.EndpointTelemetriaDto;
import gov.caixa.simuladorservice.dto.TelemetriaResponseDto;
import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import gov.caixa.simuladorservice.mapper.TelemetriaMapper;
import gov.caixa.simuladorservice.metric.MetricInterceptor;
import gov.caixa.simuladorservice.repository.TelemetriaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    TelemetriaRepository repository;

    @Inject
    TelemetriaMapper mapper;

    @Inject
    MetricInterceptor interceptor;

    @Transactional
    public List<TelemetriaResponseDto> listarMetricas() {

        // Gera relatório do dia em memória
        TelemetriaResponseDto relatorioDoDia = interceptor.gerarRelatorio();

        // Persiste cada endpoint no banco
        for (EndpointTelemetriaDto dto : relatorioDoDia.getListaEndpoints()) {
            TelemetriaEntity entity = mapper.mapDtoParaEntity(dto, relatorioDoDia.getDataReferencia());
            repository.persist(entity);
        }

        // Busca todos os registros agregados do banco
        List<Object[]> resultados = repository.listar();

        // Agrupa por data
        Map<LocalDate, List<EndpointTelemetriaDto>> mapPorData = new LinkedHashMap<>();
        for (Object[] r : resultados) {
            LocalDate data = (LocalDate) r[0];
            String nomeApi = (String) r[1];
            long qtd = ((Number) r[2]).longValue();
            long tempoMin = ((Number) r[4]).longValue();
            long tempoMax = ((Number) r[5]).longValue();
            long tempoMedio = Math.round(((Number) r[3]).doubleValue());
            double percentualSucesso = Math.round(((Number) r[6]).doubleValue() * 100.0) / 100.0;

            EndpointTelemetriaDto dto = new EndpointTelemetriaDto(
                    nomeApi,
                    qtd,
                    tempoMedio,
                    tempoMin,
                    tempoMax,
                    percentualSucesso
            );

            mapPorData.computeIfAbsent(data, k -> new ArrayList<>()).add(dto);
        }

        // Monta lista final de TelemetriaResponseDto
        List<TelemetriaResponseDto> listaFinal = new ArrayList<>();
        for (Map.Entry<LocalDate, List<EndpointTelemetriaDto>> entry : mapPorData.entrySet()) {
            listaFinal.add(new TelemetriaResponseDto(entry.getKey(), entry.getValue()));
        }

        return listaFinal;
    }
}
