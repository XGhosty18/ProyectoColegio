package org.sge.backend.dto.response;

import java.math.BigDecimal;

public record NotaResponse(
    Long id, BigDecimal valor, String observacion,
    Long evaluacionId, String evaluacionNombre,
    Long alumnoId, String alumnoNombre
) {}
