package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
    @NotNull Long alumnoId,
    @NotNull String estadoCodigo,
    String motivo,
    String referenciaDocumento
) {}
