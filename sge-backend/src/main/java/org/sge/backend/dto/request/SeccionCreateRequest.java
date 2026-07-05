package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeccionCreateRequest(
    @NotBlank String nombre,
    @NotNull Integer capacidad,
    @NotNull Long gradoId
) {}
