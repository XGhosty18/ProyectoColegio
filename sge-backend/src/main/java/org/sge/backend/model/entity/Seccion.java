package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "secciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seccion extends AuditableEntity {

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false)
    private Integer capacidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grado_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Grado grado;
}
