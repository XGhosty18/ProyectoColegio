package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record HorarioBloqueCreateRequest(
    @NotNull Long cursoId,
    Long aulaId,
    @NotNull Integer diaSemana,
    @NotNull LocalTime horaInicio,
    @NotNull LocalTime horaFin
) {}
