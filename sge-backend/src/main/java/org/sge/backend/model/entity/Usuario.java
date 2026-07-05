package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Usuario extends AuditableEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "last_login")
    private Instant lastLogin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Persona persona;
}
