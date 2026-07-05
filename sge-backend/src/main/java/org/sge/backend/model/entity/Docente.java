package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "docentes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Docente extends Persona {

    @Column(name = "codigo_empleado", nullable = false, length = 20, unique = true)
    private String codigoEmpleado;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(name = "tipo_contrato", length = 30)
    private String tipoContrato;

    @Column(name = "carga_horaria_max")
    private Integer cargaHorariaMax = 40;
}
