package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ConceptoPagoCreateRequest(
    @NotBlank String nombre,
    @NotNull BigDecimal montoBase,
    String periodicidad,
    Long gradoId
) {}
