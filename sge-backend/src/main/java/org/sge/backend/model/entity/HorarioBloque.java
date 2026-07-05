package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "horario_bloques", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"curso_id", "dia_semana", "hora_inicio"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HorarioBloque extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Aula aula;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
}
