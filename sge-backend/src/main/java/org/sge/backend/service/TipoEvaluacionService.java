package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.TipoEvaluacionCreateRequest;
import org.sge.backend.dto.response.TipoEvaluacionResponse;
import org.sge.backend.model.entity.TipoEvaluacion;
import org.sge.backend.repository.TipoEvaluacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoEvaluacionService {
    private final TipoEvaluacionRepository repository;

    @Transactional(readOnly = true)
    public TipoEvaluacionResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de evaluación no encontrado")));
    }

    @Transactional(readOnly = true)
    public List<TipoEvaluacionResponse> listar() {
        return repository.findAll().stream().map(t -> new TipoEvaluacionResponse(t.getId(), t.getNombre(), t.getPesoPorcentaje(), t.getOrden())).toList();
    }

    @Transactional
    public TipoEvaluacionResponse crear(TipoEvaluacionCreateRequest req) {
        var t = TipoEvaluacion.builder()
            .nombre(req.nombre()).pesoPorcentaje(req.pesoPorcentaje()).orden(req.orden()).build();
        return toResponse(repository.save(t));
    }

    @Transactional
    public TipoEvaluacionResponse actualizar(Long id, TipoEvaluacionCreateRequest req) {
        var t = repository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de evaluación no encontrado"));
        t.setNombre(req.nombre());
        t.setPesoPorcentaje(req.pesoPorcentaje());
        t.setOrden(req.orden());
        return toResponse(repository.save(t));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Tipo de evaluación no encontrado");
        repository.deleteById(id);
    }

    private TipoEvaluacionResponse toResponse(TipoEvaluacion t) {
        return new TipoEvaluacionResponse(t.getId(), t.getNombre(), t.getPesoPorcentaje(), t.getOrden());
    }
}
