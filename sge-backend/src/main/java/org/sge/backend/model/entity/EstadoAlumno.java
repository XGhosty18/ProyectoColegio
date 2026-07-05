package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estados_alumno")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoAlumno extends AuditableEntity {

    @Column(nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "es_terminal")
    @Builder.Default
    private Boolean esTerminal = false;

    @Column(name = "es_transitorio")
    @Builder.Default
    private Boolean esTransitorio = false;

    @Column(name = "permiso_acceso")
    @Builder.Default
    private Boolean permisoAcceso = true;

    @OneToMany(mappedBy = "estadoOrigen", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<TransicionEstado> transicionesOrigen = new ArrayList<>();

    @OneToMany(mappedBy = "estadoDestino", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<TransicionEstado> transicionesDestino = new ArrayList<>();
}
