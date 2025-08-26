package gov.caixa.simuladorservice.mapper;

import gov.caixa.simuladorservice.dto.TelemetriaRequestDto;
import gov.caixa.simuladorservice.entity.simulacao.TelemetriaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "cdi")
public interface TelemetriaMapper {

    @Mapping(target = "nomeApi", source = "dto.nomeApi")
    @Mapping(target = "tempoMs", source = "dto.tempoMs")
    @Mapping(target = "sucesso", expression = "java(dto.getSucesso() != null && dto.getSucesso() > 0 ? 1 : 0)")
    @Mapping(target = "data", expression = "java(data != null ? data : java.time.LocalDate.now())")
    TelemetriaEntity toEntity(TelemetriaRequestDto dto, LocalDate data);
}
