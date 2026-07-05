package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "conceptos_pago")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConceptoPago extends AuditableEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "monto_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoBase;

    @Column(length = 30)
    private String periodicidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grado_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Grado grado;
}
