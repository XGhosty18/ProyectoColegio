package org.sge.backend.dto.response;

import java.time.Instant;

public record HistorialEstadoResponse(
    Long id, String estadoOrigenCodigo, String estadoDestinoCodigo,
    String estadoDestinoNombre, String motivo, Instant fechaCambio,
    String registradoPor, String referenciaDocumento
) {}
