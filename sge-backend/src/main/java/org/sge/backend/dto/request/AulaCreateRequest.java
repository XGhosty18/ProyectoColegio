package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sge.backend.model.enums.TipoAula;

public record AulaCreateRequest(
    @NotBlank String nombre,
    @NotBlank String codigo,
    @NotNull Integer capacidad,
    @NotNull TipoAula tipo
) {}
