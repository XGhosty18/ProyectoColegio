package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.EstadoBimestre;

import java.time.LocalDate;

@Entity
@Table(name = "bimestres", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"periodo_id", "numero"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bimestre extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private PeriodoAcademico periodo;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoBimestre estado = EstadoBimestre.ABIERTO;
}
