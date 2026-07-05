package org.sge.backend.dto.response;

import java.time.LocalDate;

public record PeriodoResponse(
    Long id,
    String nombre,
    String codigo,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    String estado
) {}
