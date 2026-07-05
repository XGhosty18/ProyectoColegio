package org.sge.backend.dto.response;

public record AlertaFaltasResponse(Long id, Long alumnoId, String alumnoNombre, Integer cantidadConsecutivas,
                                    String nivel, String estado, String fechas, Boolean resuelta) {}
