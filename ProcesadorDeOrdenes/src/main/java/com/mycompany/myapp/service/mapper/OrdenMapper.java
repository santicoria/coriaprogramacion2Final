package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Orden;
import com.mycompany.myapp.service.dto.OrdenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Orden} and its DTO {@link OrdenDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrdenMapper extends EntityMapper<OrdenDTO, Orden> {}
