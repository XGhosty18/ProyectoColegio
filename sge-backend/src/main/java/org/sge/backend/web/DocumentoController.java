package org.sge.backend.web;

import lombok.RequiredArgsConstructor;
import org.sge.backend.service.DocumentoService;
import org.sge.backend.dto.response.DocumentoResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
public class DocumentoController {
    private final DocumentoService service;
    @GetMapping("/{entidadTipo}/{entidadId}") public List<DocumentoResponse> listar(@PathVariable String entidadTipo, @PathVariable Long entidadId) { return service.listarPorEntidad(entidadTipo, entidadId); }

    @GetMapping("/id/{id}")
    public DocumentoResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentoResponse upload(@RequestParam String entidadTipo, @RequestParam Long entidadId, @RequestParam String tipoDoc, @RequestParam MultipartFile file) throws Exception {
        return service.upload(entidadTipo, entidadId, tipoDoc, file);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentoResponse actualizar(@PathVariable Long id, @RequestParam String tipoDoc, @RequestParam MultipartFile file) throws Exception {
        return service.actualizar(id, tipoDoc, file);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
