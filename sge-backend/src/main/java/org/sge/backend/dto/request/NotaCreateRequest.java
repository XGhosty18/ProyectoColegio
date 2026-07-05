package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record NotaCreateRequest(
    @NotNull Long evaluacionId,
    @NotNull Long alumnoId,
    @NotNull BigDecimal valor,
    String observacion
) {}
