package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoCreateRequest(
    @NotNull Long alumnoId,
    @NotNull Long cronogramaPagoId,
    @NotNull BigDecimal monto,
    String metodo,
    String referencia,
    LocalDate fechaPago
) {}
