package org.sge.backend.dto.response;

import java.time.LocalTime;

public record HorarioBloqueResponse(
    Long id, Integer diaSemana, LocalTime horaInicio, LocalTime horaFin,
    Long cursoId, String cursoNombre,
    Long aulaId, String aulaNombre,
    Long docenteId, String docenteNombre,
    String materiaNombre, Long materiaId
) {}
