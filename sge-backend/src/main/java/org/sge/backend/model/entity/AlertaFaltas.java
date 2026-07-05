package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "alertas_faltas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertaFaltas extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Curso curso;

    @Column(name = "cantidad_consecutivas", nullable = false)
    private Integer cantidadConsecutivas;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String nivel = "ALTA";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "NUEVA";

    @JdbcTypeCode(SqlTypes.JSON)
    private String fechas;

    @Column(name = "resuelta_at")
    private Instant resueltaAt;
}
