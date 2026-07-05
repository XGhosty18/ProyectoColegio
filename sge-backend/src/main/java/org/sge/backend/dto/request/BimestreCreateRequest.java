package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sge.backend.model.enums.EstadoBimestre;
import java.time.LocalDate;

public record BimestreCreateRequest(
    @NotNull Long periodoId,
    @NotNull Integer numero,
    @NotBlank String nombre,
    @NotNull LocalDate fechaInicio,
    @NotNull LocalDate fechaFin,
    EstadoBimestre estado
) {}
