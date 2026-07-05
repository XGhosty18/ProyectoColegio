package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.TipoMateria;

@Entity
@Table(name = "materias", uniqueConstraints = {
    @UniqueConstraint(columnNames = "codigo")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Materia extends AuditableEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(name = "horas_semanales_req")
    private Integer horasSemanalesReq;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMateria tipo;
}
