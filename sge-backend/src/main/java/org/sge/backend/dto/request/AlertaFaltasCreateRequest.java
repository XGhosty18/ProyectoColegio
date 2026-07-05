package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record AlertaFaltasCreateRequest(
    @NotNull Long alumnoId,
    @NotNull Long cursoId,
    @NotNull Integer cantidadConsecutivas,
    String nivel,
    String fechas
) {}
