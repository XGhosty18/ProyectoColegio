package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles_permisos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class RolPermiso extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Permiso permiso;
}
