package org.sge.backend.dto.response;

import org.sge.backend.model.enums.TipoAsistencia;

public record AsistenciaResponse(
    Long id, TipoAsistencia tipoAsistencia, Integer minutosTardanza, String observacion,
    Long sesionId, Long alumnoId, String alumnoNombre
) {}
