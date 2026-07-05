package org.sge.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.sge.backend.model.enums.EstadoBimestre;

public record BimestreUpdateRequest(@NotNull LocalDate fechaInicio, @NotNull LocalDate fechaFin, EstadoBimestre estado) {}
