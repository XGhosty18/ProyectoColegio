package org.sge.backend.dto.response;

import java.time.LocalDate;

public record PadreResponse(
    Long id, String nombres, String apellidos, String dni, LocalDate fechaNac,
    String genero, String telefono, String direccion,
    String parentesco, Boolean esTitular
) {}
