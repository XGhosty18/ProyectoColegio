package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.NivelEducativo;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grados")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Grado extends AuditableEntity {

    @Column(nullable = false, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelEducativo nivel;

    @Column(nullable = false)
    private Integer orden;

    @Column(name = "capacidad_max")
    private Integer capacidadMax;

    @OneToMany(mappedBy = "grado", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Seccion> secciones = new ArrayList<>();
}
