package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sge.backend.model.enums.TipoMateria;

public record MateriaCreateRequest(
    @NotBlank String nombre,
    @NotBlank String codigo,
    @NotNull Integer horasSemanalesReq,
    @NotNull TipoMateria tipo
) {}
