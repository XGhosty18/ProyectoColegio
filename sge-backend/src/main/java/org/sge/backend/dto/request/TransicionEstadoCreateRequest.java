package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransicionEstadoCreateRequest(
    @NotNull Long estadoOrigenId,
    @NotNull Long estadoDestinoId,
    @NotBlank String codigoGatillante,
    Boolean esAutomatica,
    Boolean requiereAdmin,
    Boolean requiereConsejo,
    Boolean notificaPadre
) {}
