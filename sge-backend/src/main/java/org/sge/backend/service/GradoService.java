package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.GradoCreateRequest;
import org.sge.backend.dto.response.GradoResponse;
import org.sge.backend.model.entity.Grado;
import org.sge.backend.model.enums.NivelEducativo;
import org.sge.backend.repository.GradoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradoService {

    private final GradoRepository repository;

    @Transactional(readOnly = true)
    public List<GradoResponse> listar(NivelEducativo nivel) {
        if (nivel != null) return repository.findByNivelOrderByOrden(nivel).stream().map(this::toResponse).toList();
        return repository.findAllByOrderByOrden().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GradoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Grado no encontrado")));
    }

    @Transactional
    public GradoResponse crear(GradoCreateRequest req) {
        return toResponse(repository.save(Grado.builder().nombre(req.nombre()).nivel(req.nivel()).orden(req.orden()).capacidadMax(req.capacidadMax()).build()));
    }

    @Transactional
    public GradoResponse actualizar(Long id, GradoCreateRequest req) {
        var g = repository.findById(id).orElseThrow(() -> new RuntimeException("Grado no encontrado"));
        g.setNombre(req.nombre());
        g.setNivel(req.nivel());
        g.setOrden(req.orden());
        g.setCapacidadMax(req.capacidadMax());
        return toResponse(repository.save(g));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Grado no encontrado");
        repository.deleteById(id);
    }

    private GradoResponse toResponse(Grado g) {
        return new GradoResponse(g.getId(), g.getNombre(), g.getNivel(), g.getOrden(), g.getCapacidadMax());
    }
}
