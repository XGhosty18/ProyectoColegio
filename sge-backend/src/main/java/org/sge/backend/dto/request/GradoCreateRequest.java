package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sge.backend.model.enums.NivelEducativo;

public record GradoCreateRequest(
    @NotBlank String nombre,
    @NotNull NivelEducativo nivel,
    @NotNull Integer orden,
    Integer capacidadMax
) {}
