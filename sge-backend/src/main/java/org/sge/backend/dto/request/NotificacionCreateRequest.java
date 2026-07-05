package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificacionCreateRequest(
    @NotNull Long usuarioId,
    @NotBlank String titulo,
    @NotBlank String cuerpo,
    String tipo,
    String entidadTipo,
    Long entidadId
) {}
