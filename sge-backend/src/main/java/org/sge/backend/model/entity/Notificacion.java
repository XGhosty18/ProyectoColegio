package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notificaciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacion extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String cuerpo;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String tipo = "INFO";

    @Builder.Default
    private Boolean leida = false;

    @Column(name = "entidad_tipo", length = 50)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Long entidadId;
}
