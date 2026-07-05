package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles_usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class UsuarioRol extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Rol rol;
}
