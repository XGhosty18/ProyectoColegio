package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.TipoAsistencia;

@Entity
@Table(name = "asistencias_alumno", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sesion_id", "alumno_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AsistenciaAlumno extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private SesionClase sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Alumno alumno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asistencia", nullable = false, length = 20)
    private TipoAsistencia tipoAsistencia;

    @Column(name = "minutos_tardanza")
    private Integer minutosTardanza;

    @Column(length = 500)
    private String observacion;
}
