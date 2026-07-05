package org.sge.backend.dto.response;

public record StripePaymentIntentResponse(
    String clientSecret,
    String paymentIntentId,
    Long amount,
    String currency
) {}
