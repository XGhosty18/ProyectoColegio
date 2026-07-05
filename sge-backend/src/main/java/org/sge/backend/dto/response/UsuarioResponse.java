package org.sge.backend.dto.response;

import java.util.List;

public record UsuarioResponse(
    Long id, String username, String email, Boolean enabled,
    Long personaId, String personaNombre, List<String> roles
) {}
