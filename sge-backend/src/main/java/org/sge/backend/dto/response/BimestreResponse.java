package org.sge.backend.dto.response;

public record BimestreResponse(
    Long id, Integer numero, String nombre, java.time.LocalDate fechaInicio,
    java.time.LocalDate fechaFin, String estado, Long periodoId
) {}
