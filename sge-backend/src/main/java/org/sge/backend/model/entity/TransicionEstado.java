package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transiciones_estado", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"estado_origen_id", "estado_destino_id", "codigo_gatillante"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransicionEstado extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_origen_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EstadoAlumno estadoOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_destino_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private EstadoAlumno estadoDestino;

    @Column(name = "codigo_gatillante", nullable = false, length = 30)
    private String codigoGatillante;

    @Column(name = "es_automatica")
    @Builder.Default
    private Boolean esAutomatica = false;

    @Column(name = "requiere_admin")
    @Builder.Default
    private Boolean requiereAdmin = true;

    @Column(name = "requiere_consejo")
    @Builder.Default
    private Boolean requiereConsejo = false;

    @Column(name = "notifica_padre")
    @Builder.Default
    private Boolean notificaPadre = false;
}
