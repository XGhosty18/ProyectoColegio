package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CronogramaPagoCreateRequest(
    @NotNull Long alumnoId,
    @NotNull Long conceptoPagoId,
    @NotNull Long periodoId,
    @NotNull BigDecimal monto,
    @NotNull LocalDate fechaVencimiento
) {}
