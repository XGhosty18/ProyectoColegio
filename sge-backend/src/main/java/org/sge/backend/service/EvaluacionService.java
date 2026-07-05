package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.EvaluacionCreateRequest;
import org.sge.backend.dto.response.EvaluacionResponse;
import org.sge.backend.model.entity.Evaluacion;
import org.sge.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionService {
    private final EvaluacionRepository repository;
    private final CursoRepository cursoRepo;
    private final BimestreRepository bimestreRepo;
    private final TipoEvaluacionRepository tipoEvalRepo;

    @Transactional(readOnly = true) public List<EvaluacionResponse> listarPorCurso(Long cursoId) { return repository.findByCursoId(cursoId).stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public EvaluacionResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Evaluación no encontrada")));
    }

    @Transactional
    public EvaluacionResponse actualizar(Long id, EvaluacionCreateRequest req) {
        var e = repository.findById(id).orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        var bimestre = bimestreRepo.findById(req.bimestreId()).orElseThrow();
        var tipo = tipoEvalRepo.findById(req.tipoEvaluacionId()).orElseThrow();
        e.setCurso(curso);
        e.setBimestre(bimestre);
        e.setTipoEvaluacion(tipo);
        e.setNombre(req.nombre());
        e.setFecha(req.fecha());
        e.setPonderacionOverride(req.ponderacionOverride());
        return toResponse(repository.save(e));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Evaluación no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public EvaluacionResponse crear(EvaluacionCreateRequest req) {
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        var bimestre = bimestreRepo.findById(req.bimestreId()).orElseThrow();
        var tipo = tipoEvalRepo.findById(req.tipoEvaluacionId()).orElseThrow();
        var e = Evaluacion.builder().curso(curso).bimestre(bimestre).tipoEvaluacion(tipo)
            .nombre(req.nombre()).fecha(req.fecha()).ponderacionOverride(req.ponderacionOverride()).build();
        return toResponse(repository.save(e));
    }

    private EvaluacionResponse toResponse(Evaluacion e) {
        return new EvaluacionResponse(e.getId(), e.getNombre(), e.getFecha(), e.getPonderacionOverride(),
            e.getCurso().getId(), e.getCurso().getMateria().getNombre(),
            e.getBimestre().getId(), e.getBimestre().getNombre(),
            e.getTipoEvaluacion().getId(), e.getTipoEvaluacion().getNombre());
    }
}
