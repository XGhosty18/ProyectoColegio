package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.NotaCreateRequest;
import org.sge.backend.dto.response.NotaResponse;
import org.sge.backend.model.entity.Nota;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.EvaluacionRepository;
import org.sge.backend.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository repository;
    private final EvaluacionRepository evaluacionRepo;
    private final AlumnoRepository alumnoRepo;

    @Transactional(readOnly = true)
    public List<NotaResponse> listarPorEvaluacion(Long evaluacionId) {
        return repository.findByEvaluacionId(evaluacionId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotaResponse> listarPorAlumnoYBimestre(Long alumnoId, Long bimestreId) {
        return repository.findByAlumnoIdAndEvaluacionBimestreId(alumnoId, bimestreId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NotaResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Nota no encontrada")));
    }

    @Transactional
    public NotaResponse actualizar(Long id, NotaCreateRequest req) {
        var n = repository.findById(id).orElseThrow(() -> new RuntimeException("Nota no encontrada"));
        var evaluacion = evaluacionRepo.findById(req.evaluacionId()).orElseThrow();
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        n.setEvaluacion(evaluacion);
        n.setAlumno(alumno);
        n.setValor(req.valor());
        n.setObservacion(req.observacion());
        return toResponse(repository.save(n));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Nota no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public void crearMasivo(List<NotaCreateRequest> requests) {
        for (var req : requests) {
            var evaluacion = evaluacionRepo.findById(req.evaluacionId()).orElseThrow();
            var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
            repository.save(Nota.builder().evaluacion(evaluacion).alumno(alumno)
                .valor(req.valor()).observacion(req.observacion()).build());
        }
    }

    @Transactional
    public NotaResponse crear(NotaCreateRequest req) {
        var evaluacion = evaluacionRepo.findById(req.evaluacionId()).orElseThrow();
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var nota = Nota.builder().evaluacion(evaluacion).alumno(alumno)
            .valor(req.valor()).observacion(req.observacion()).build();
        return toResponse(repository.save(nota));
    }

    private NotaResponse toResponse(Nota n) {
        return new NotaResponse(
            n.getId(), n.getValor(), n.getObservacion(),
            n.getEvaluacion().getId(), n.getEvaluacion().getNombre(),
            n.getAlumno().getId(), n.getAlumno().getNombres() + " " + n.getAlumno().getApellidos()
        );
    }
}
