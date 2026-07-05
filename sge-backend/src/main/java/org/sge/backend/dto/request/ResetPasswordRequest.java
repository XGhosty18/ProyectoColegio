package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank String email,
    @NotBlank String code,
    @NotBlank String newPassword
) {}
