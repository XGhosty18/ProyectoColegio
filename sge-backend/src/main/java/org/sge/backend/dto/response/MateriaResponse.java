package org.sge.backend.dto.response;

import org.sge.backend.model.enums.TipoMateria;

public record MateriaResponse(Long id, String nombre, String codigo, Integer horasSemanalesReq, TipoMateria tipo) {}
