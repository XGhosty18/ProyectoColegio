package org.sge.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Documento extends AuditableEntity {

    @Column(name = "entidad_tipo", nullable = false, length = 50)
    private String entidadTipo;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @Column(name = "tipo_doc", nullable = false, length = 30)
    private String tipoDoc;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "generado_por", length = 50)
    private String generadoPor;
}
