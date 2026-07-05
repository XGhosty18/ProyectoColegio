package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record CursoCreateRequest(
    @NotNull Long periodoId,
    @NotNull Long gradoId,
    @NotNull Long seccionId,
    @NotNull Long materiaId,
    Long docenteId,
    Long aulaId
) {}
