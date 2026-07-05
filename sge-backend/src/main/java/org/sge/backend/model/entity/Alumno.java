package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alumnos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Alumno extends Persona {

    @Column(name = "codigo_estudiante", nullable = false, length = 20, unique = true)
    private String codigoEstudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_actual_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EstadoAlumno estadoActual;

    @Column(name = "sub_estado", length = 30)
    private String subEstado;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_ultimo_estado")
    private Instant fechaUltimoEstado;

    @ManyToMany
    @JoinTable(
        name = "alumnos_padres",
        joinColumns = @JoinColumn(name = "alumno_id"),
        inverseJoinColumns = @JoinColumn(name = "padre_id")
    )
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<Padre> padres = new HashSet<>();
}
