package org.sge.backend.dto.response;

import org.sge.backend.model.enums.NivelEducativo;

public record GradoResponse(Long id, String nombre, NivelEducativo nivel, Integer orden, Integer capacidadMax) {}
