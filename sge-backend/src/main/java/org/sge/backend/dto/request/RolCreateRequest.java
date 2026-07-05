package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RolCreateRequest(
    @NotBlank String codigo,
    @NotBlank String nombre,
    List<Long> permisoIds
) {}
