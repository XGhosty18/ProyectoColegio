package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_evaluacion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoEvaluacion extends AuditableEntity {

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "peso_porcentaje", nullable = false)
    private Double pesoPorcentaje;

    @Column(nullable = false)
    private Integer orden;
}
