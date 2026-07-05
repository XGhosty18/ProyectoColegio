package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "historial_estados_alumno")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialEstadoAlumno extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_anterior_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EstadoAlumno estadoAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_nuevo_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EstadoAlumno estadoNuevo;

    @Column(nullable = false, length = 1000)
    private String motivo;

    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    @Column(name = "fecha_cambio", nullable = false)
    private Instant fechaCambio;
}
