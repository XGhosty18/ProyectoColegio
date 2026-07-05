package org.sge.backend.dto.response;

public record TransicionEstadoResponse(
    Long id, Long estadoOrigenId, String estadoOrigenNombre,
    Long estadoDestinoId, String estadoDestinoNombre,
    String codigoGatillante, Boolean esAutomatica,
    Boolean requiereAdmin, Boolean requiereConsejo, Boolean notificaPadre
) {}
