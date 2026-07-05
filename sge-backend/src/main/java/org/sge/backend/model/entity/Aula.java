package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sge.backend.model.enums.TipoAula;

@Entity
@Table(name = "aulas", uniqueConstraints = {
    @UniqueConstraint(columnNames = "codigo")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Aula extends AuditableEntity {

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAula tipo;
}
