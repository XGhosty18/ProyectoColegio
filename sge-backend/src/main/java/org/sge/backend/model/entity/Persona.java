package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "personas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class Persona extends AuditableEntity {

    @Column(nullable = false, length = 8, unique = true)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(length = 1)
    private String genero;

    @Column(length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;
}
