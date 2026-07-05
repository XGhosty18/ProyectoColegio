package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EvaluacionCreateRequest(
    @NotNull Long cursoId, @NotNull Long bimestreId, @NotNull Long tipoEvaluacionId,
    @NotBlank String nombre, @NotNull LocalDate fecha, Double ponderacionOverride
) {}
