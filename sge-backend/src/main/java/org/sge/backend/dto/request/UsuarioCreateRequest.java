package org.sge.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record UsuarioCreateRequest(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank String password,
    Long personaId,
    List<Long> rolIds
) {}
