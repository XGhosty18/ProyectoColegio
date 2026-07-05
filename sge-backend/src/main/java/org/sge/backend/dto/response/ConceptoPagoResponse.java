package org.sge.backend.dto.response;

import java.math.BigDecimal;

public record ConceptoPagoResponse(Long id, String nombre, BigDecimal montoBase, String periodicidad, Long gradoId) {}
