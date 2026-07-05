package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.model.entity.Documento;
import org.sge.backend.repository.DocumentoRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock private DocumentoRepository repository;
    private DocumentoService service;

    @BeforeEach
    void setUp() {
        service = new DocumentoService(repository);
    }

    @Test
    void listarPorEntidad_deberiaRetornar() {
        var doc = Documento.builder().entidadTipo("ALUMNO").entidadId(1L).nombreArchivo("reporte.pdf").tipoDoc("ACTA").mimeType("application/pdf").build();
        when(repository.findByEntidadTipoAndEntidadId("ALUMNO", 1L)).thenReturn(List.of(doc));

        var res = service.listarPorEntidad("ALUMNO", 1L);

        assertEquals(1, res.size());
        assertEquals("reporte.pdf", res.getFirst().nombreArchivo());
    }

    @Test
    void upload_deberiaGuardarArchivo() throws Exception {
        var file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("doc.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(repository.save(any())).thenAnswer(inv -> {
            var d = inv.<Documento>getArgument(0);
            d.setId(1L);
            return d;
        });

        var res = service.upload("ALUMNO", 1L, "DNI", file);

        assertEquals("doc.pdf", res.nombreArchivo());
        assertEquals("DNI", res.tipoDoc());
    }
}
