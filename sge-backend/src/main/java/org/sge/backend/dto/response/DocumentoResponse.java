package org.sge.backend.dto.response;

public record DocumentoResponse(Long id, String nombreArchivo, String tipoDoc, String mimeType, Long entidadId, String entidadTipo) {}
