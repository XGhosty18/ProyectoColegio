package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PadreCreateRequest(
    @NotBlank String nombres,
    @NotBlank String apellidos,
    @NotBlank String dni,
    LocalDate fechaNac,
    String genero, String telefono, String direccion,
    String parentesco,
    Boolean esTitular
) {}
