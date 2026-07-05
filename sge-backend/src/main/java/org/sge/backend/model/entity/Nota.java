package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "notas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"evaluacion_id", "alumno_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Nota extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Alumno alumno;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal valor;

    @Column(length = 500)
    private String observacion;

    @Column(name = "registrado_por", length = 50)
    private String registradoPor;
}
