package org.sge.backend.dto.response;

import java.time.LocalDate;
import java.math.BigDecimal;

public record CronogramaPagoResponse(Long id, Long alumnoId, String alumnoNombre, Long conceptoPagoId,
                                      String conceptoNombre, BigDecimal monto, LocalDate fechaVencimiento,
                                      String estado) {}
