package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PermisoCreateRequest(
    @NotBlank String codigo,
    @NotBlank String descripcion,
    String modulo
) {}
