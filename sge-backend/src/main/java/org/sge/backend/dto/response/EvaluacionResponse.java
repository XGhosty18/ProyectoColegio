package org.sge.backend.dto.response;

import java.time.LocalDate;

public record EvaluacionResponse(
    Long id, String nombre, LocalDate fecha, Double ponderacionOverride,
    Long cursoId, String cursoNombre,
    Long bimestreId, String bimestreNombre,
    Long tipoEvaluacionId, String tipoEvaluacionNombre
) {}
