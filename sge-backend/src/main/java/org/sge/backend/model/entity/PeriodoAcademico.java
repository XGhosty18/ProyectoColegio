package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.EstadoPeriodo;

import java.time.LocalDate;

@Entity
@Table(name = "periodos_academicos", uniqueConstraints = {
    @UniqueConstraint(columnNames = "codigo")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PeriodoAcademico extends AuditableEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPeriodo estado = EstadoPeriodo.PLANIFICACION;
}
