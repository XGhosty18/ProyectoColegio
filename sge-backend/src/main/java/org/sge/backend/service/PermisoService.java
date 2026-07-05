package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PermisoCreateRequest;
import org.sge.backend.dto.response.PermisoResponse;
import org.sge.backend.model.entity.Permiso;
import org.sge.backend.repository.PermisoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermisoService {
    private final PermisoRepository repository;

    @Transactional(readOnly = true)
    public List<PermisoResponse> listar() {
        return repository.findAll().stream().map(p -> new PermisoResponse(p.getId(), p.getCodigo(), p.getDescripcion(), p.getModulo())).toList();
    }

    @Transactional(readOnly = true)
    public PermisoResponse obtenerPorId(Long id) {
        var p = repository.findById(id).orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
        return new PermisoResponse(p.getId(), p.getCodigo(), p.getDescripcion(), p.getModulo());
    }

    @Transactional
    public PermisoResponse crear(PermisoCreateRequest req) {
        var p = Permiso.builder().codigo(req.codigo()).descripcion(req.descripcion()).modulo(req.modulo()).build();
        return toResponse(repository.save(p));
    }

    @Transactional
    public PermisoResponse actualizar(Long id, PermisoCreateRequest req) {
        var p = repository.findById(id).orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
        p.setCodigo(req.codigo());
        p.setDescripcion(req.descripcion());
        p.setModulo(req.modulo());
        return toResponse(repository.save(p));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Permiso no encontrado");
        repository.deleteById(id);
    }

    private PermisoResponse toResponse(Permiso p) {
        return new PermisoResponse(p.getId(), p.getCodigo(), p.getDescripcion(), p.getModulo());
    }
}
