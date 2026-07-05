package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.SeccionCreateRequest;
import org.sge.backend.dto.response.SeccionResponse;
import org.sge.backend.model.entity.Seccion;
import org.sge.backend.repository.GradoRepository;
import org.sge.backend.repository.SeccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeccionService {
    private final SeccionRepository repository;
    private final GradoRepository gradoRepo;

    @Transactional(readOnly = true)
    public List<SeccionResponse> listar(Long gradoId) {
        if (gradoId != null) return repository.findByGradoId(gradoId).stream().map(this::toResponse).toList();
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SeccionResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Sección no encontrada")));
    }

    @Transactional
    public SeccionResponse crear(SeccionCreateRequest req) {
        var grado = gradoRepo.findById(req.gradoId()).orElseThrow();
        return toResponse(repository.save(Seccion.builder().nombre(req.nombre()).capacidad(req.capacidad()).grado(grado).build()));
    }

    @Transactional
    public SeccionResponse actualizar(Long id, SeccionCreateRequest req) {
        var s = repository.findById(id).orElseThrow(() -> new RuntimeException("Sección no encontrada"));
        var grado = gradoRepo.findById(req.gradoId()).orElseThrow();
        s.setNombre(req.nombre());
        s.setCapacidad(req.capacidad());
        s.setGrado(grado);
        return toResponse(repository.save(s));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Sección no encontrada");
        repository.deleteById(id);
    }

    private SeccionResponse toResponse(Seccion s) {
        return new SeccionResponse(s.getId(), s.getNombre(), s.getCapacidad(), s.getGrado().getId(), s.getGrado().getNombre());
    }
}
