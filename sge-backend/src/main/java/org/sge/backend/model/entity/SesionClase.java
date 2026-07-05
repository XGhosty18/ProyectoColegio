package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.EstadoSesion;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sesiones_clase")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SesionClase extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_bloque_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private HorarioBloque horarioBloque;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(length = 300)
    private String tema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoSesion estado = EstadoSesion.PROGRAMADA;
}
