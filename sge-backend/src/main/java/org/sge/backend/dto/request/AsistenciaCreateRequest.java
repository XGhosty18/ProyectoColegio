package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import org.sge.backend.model.enums.TipoAsistencia;

public record AsistenciaCreateRequest(
    @NotNull Long sesionId,
    @NotNull Long alumnoId,
    @NotNull TipoAsistencia tipoAsistencia,
    Integer minutosTardanza,
    String observacion
) {}
