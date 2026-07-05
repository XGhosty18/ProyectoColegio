package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record SesionClaseCreateRequest(
    @NotNull Long cursoId, Long horarioBloqueId,
    @NotNull LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
    @NotBlank String tema
) {}
