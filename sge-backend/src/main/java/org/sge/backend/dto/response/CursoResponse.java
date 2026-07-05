package org.sge.backend.dto.response;

public record CursoResponse(
    Long id, String estado,
    Long periodoId, String periodoNombre,
    Long gradoId, String gradoNombre,
    Long seccionId, String seccionNombre,
    Long materiaId, String materiaNombre,
    Long docenteId, String docenteNombre,
    Long aulaId, String aulaNombre
) {}
