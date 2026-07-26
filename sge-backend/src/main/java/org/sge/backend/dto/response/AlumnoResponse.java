package org.sge.backend.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AlumnoResponse(
    Long id, String nombres, String apellidos, String dni, LocalDate fechaNac,
    String genero, String telefono, String direccion,
    String codigoEstudiante, Long estadoActualId, String estadoActualCodigo,
    String estadoActualNombre, String subEstado, LocalDate fechaIngreso,
    List<PadreResponse> padres
) {}
