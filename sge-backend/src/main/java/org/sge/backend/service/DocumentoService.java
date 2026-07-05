package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.response.DocumentoResponse;
import org.sge.backend.model.entity.Documento;
import org.sge.backend.repository.DocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentoService {
    private final DocumentoRepository repository;
    private final Path uploadDir = Paths.get("uploads");

    @Transactional(readOnly = true) public List<DocumentoResponse> listarPorEntidad(String entidadTipo, Long entidadId) {
        return repository.findByEntidadTipoAndEntidadId(entidadTipo, entidadId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DocumentoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Documento no encontrado")));
    }

    @Transactional
    public DocumentoResponse actualizar(Long id, String tipoDoc, MultipartFile file) throws IOException {
        var d = repository.findById(id).orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        Files.createDirectories(uploadDir);
        var name = UUID.randomUUID() + "_" + file.getOriginalFilename();
        var path = uploadDir.resolve(name);
        Files.copy(file.getInputStream(), path);
        d.setTipoDoc(tipoDoc);
        d.setNombreArchivo(file.getOriginalFilename());
        d.setUrl(path.toString());
        d.setMimeType(file.getContentType());
        return toResponse(repository.save(d));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Documento no encontrado");
        repository.deleteById(id);
    }

    @Transactional
    public DocumentoResponse upload(String entidadTipo, Long entidadId, String tipoDoc, MultipartFile file) throws IOException {
        Files.createDirectories(uploadDir);
        var name = UUID.randomUUID() + "_" + file.getOriginalFilename();
        var path = uploadDir.resolve(name);
        Files.copy(file.getInputStream(), path);

        var doc = Documento.builder()
            .entidadTipo(entidadTipo).entidadId(entidadId).tipoDoc(tipoDoc)
            .nombreArchivo(file.getOriginalFilename()).url(path.toString())
            .mimeType(file.getContentType()).generadoPor("SYSTEM").build();
        return toResponse(repository.save(doc));
    }

    private DocumentoResponse toResponse(Documento d) {
        return new DocumentoResponse(d.getId(), d.getNombreArchivo(), d.getTipoDoc(), d.getMimeType(), d.getEntidadId(), d.getEntidadTipo());
    }
}
