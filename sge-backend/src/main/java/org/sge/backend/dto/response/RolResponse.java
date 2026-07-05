package org.sge.backend.dto.response;

import java.util.List;

public record RolResponse(Long id, String codigo, String nombre, List<String> permisos) {}
