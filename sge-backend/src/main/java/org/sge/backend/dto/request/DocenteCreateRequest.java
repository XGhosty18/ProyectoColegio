package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DocenteCreateRequest(
    @NotBlank String nombres,
    @NotBlank String apellidos,
    @NotBlank String dni,
    @NotNull LocalDate fechaNac,
    String genero, String telefono, String direccion,
    @NotBlank String codigoEmpleado,
    @NotBlank String especialidad,
    String tipoContrato,
    Integer cargaHorariaMax
) {}
