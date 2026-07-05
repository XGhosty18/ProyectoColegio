package org.sge.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoResponse(Long id, Long alumnoId, String alumnoNombre, Long cronogramaPagoId,
                            BigDecimal monto, String metodo, String referencia, LocalDate fechaPago) {}
