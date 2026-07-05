package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permisos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Permiso extends AuditableEntity {

    @Column(nullable = false, length = 30, unique = true)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(length = 30)
    private String modulo;
}
