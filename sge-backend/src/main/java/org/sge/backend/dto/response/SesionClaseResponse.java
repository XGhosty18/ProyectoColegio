package org.sge.backend.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record SesionClaseResponse(
    Long id, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String tema, String estado,
    Long cursoId, Long aulaId
) {}
