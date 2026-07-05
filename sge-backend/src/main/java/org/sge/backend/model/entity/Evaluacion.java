package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "evaluaciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"curso_id", "bimestre_id", "tipo_evaluacion_id", "nombre"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evaluacion extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bimestre_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Bimestre bimestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_evaluacion_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private TipoEvaluacion tipoEvaluacion;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "ponderacion_override")
    private Double ponderacionOverride;
}
