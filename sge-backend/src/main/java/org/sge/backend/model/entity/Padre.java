package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "padres")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Padre extends Persona {

    @Column(length = 20)
    private String parentesco;

    @Column(name = "es_titular")
    private Boolean esTitular = false;

    @ManyToMany(mappedBy = "padres")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<Alumno> hijos = new HashSet<>();
}
