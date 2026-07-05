package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TipoEvaluacionCreateRequest(
    @NotBlank String nombre,
    @NotNull Double pesoPorcentaje,
    @NotNull Integer orden
) {}
