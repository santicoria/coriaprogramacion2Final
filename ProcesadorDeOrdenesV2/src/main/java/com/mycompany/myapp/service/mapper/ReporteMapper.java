package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Reporte;
import com.mycompany.myapp.service.dto.ReporteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reporte} and its DTO {@link ReporteDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReporteMapper extends EntityMapper<ReporteDTO, Reporte> {}
