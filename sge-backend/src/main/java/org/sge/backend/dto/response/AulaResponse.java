package org.sge.backend.dto.response;

import org.sge.backend.model.enums.TipoAula;

public record AulaResponse(Long id, String nombre, String codigo, Integer capacidad, TipoAula tipo) {}
