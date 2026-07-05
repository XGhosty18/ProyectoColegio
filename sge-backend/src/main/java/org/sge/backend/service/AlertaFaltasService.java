package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AlertaFaltasCreateRequest;
import org.sge.backend.dto.response.AlertaFaltasResponse;
import org.sge.backend.model.entity.AlertaFaltas;
import org.sge.backend.repository.AlertaFaltasRepository;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaFaltasService {
    private final AlertaFaltasRepository repository;
    private final AlumnoRepository alumnoRepo;
    private final CursoRepository cursoRepo;

    @Transactional(readOnly = true)
    public AlertaFaltasResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada")));
    }

    @Transactional(readOnly = true)
    public List<AlertaFaltasResponse> pendientes() {
        return repository.findByEstado("NUEVA").stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AlertaFaltasResponse> listarPorAlumno(Long alumnoId) {
        return repository.findByAlumnoIdOrderByCreatedAtDesc(alumnoId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public AlertaFaltasResponse crear(AlertaFaltasCreateRequest req) {
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        var a = AlertaFaltas.builder()
            .alumno(alumno).curso(curso)
            .cantidadConsecutivas(req.cantidadConsecutivas())
            .nivel(req.nivel() != null ? req.nivel() : "ALTA")
            .fechas(req.fechas()).build();
        return toResponse(repository.save(a));
    }

    @Transactional
    public AlertaFaltasResponse actualizar(Long id, AlertaFaltasCreateRequest req) {
        var a = repository.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        a.setAlumno(alumno);
        a.setCurso(curso);
        a.setCantidadConsecutivas(req.cantidadConsecutivas());
        a.setNivel(req.nivel() != null ? req.nivel() : "ALTA");
        a.setFechas(req.fechas());
        return toResponse(repository.save(a));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Alerta no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public void atender(Long id) {
        var a = repository.findById(id).orElseThrow();
        a.setEstado("ATENDIDA");
        repository.save(a);
    }

    private AlertaFaltasResponse toResponse(AlertaFaltas a) {
        return new AlertaFaltasResponse(a.getId(), a.getAlumno().getId(),
            a.getAlumno().getNombres() + " " + a.getAlumno().getApellidos(),
            a.getCantidadConsecutivas(), a.getNivel(), a.getEstado(), a.getFechas(), a.getResueltaAt() != null);
    }
}
