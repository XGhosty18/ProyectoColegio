package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record StripePaymentIntentRequest(
    @NotNull Long cronogramaPagoId,
    @NotNull Long alumnoId,
    @NotNull Long usuarioId
) {}
