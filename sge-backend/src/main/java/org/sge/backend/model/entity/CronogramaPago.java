package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cronograma_pagos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CronogramaPago extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concepto_pago_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ConceptoPago conceptoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private PeriodoAcademico periodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Alumno alumno;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "PENDIENTE";
}
