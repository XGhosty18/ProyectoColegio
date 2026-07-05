package org.sge.backend.dto.response;

public record NotificacionResponse(Long id, String titulo, String cuerpo, Long usuarioId,
                                    String tipo, Boolean leida, String createdAt) {}
