package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DocumentoUploadRequest(@NotBlank String entidadTipo, @NotBlank Long entidadId, @NotBlank String tipoDoc) {}
