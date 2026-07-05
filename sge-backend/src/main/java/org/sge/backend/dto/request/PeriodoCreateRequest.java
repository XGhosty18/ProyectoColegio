package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PeriodoCreateRequest(
    @NotBlank String nombre,
    @NotBlank String codigo,
    @NotNull LocalDate fechaInicio,
    @NotNull LocalDate fechaFin
) {}
