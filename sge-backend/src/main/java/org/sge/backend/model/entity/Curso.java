package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.EstadoCurso;

@Entity
@Table(name = "cursos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"periodo_id", "grado_id", "seccion_id", "materia_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Curso extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private PeriodoAcademico periodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grado_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Seccion seccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Aula aula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoCurso estado = EstadoCurso.BORRADOR;
}
