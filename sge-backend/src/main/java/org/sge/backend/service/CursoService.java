package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.CursoCreateRequest;
import org.sge.backend.dto.response.CursoResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.*;
import org.sge.backend.model.enums.EstadoCurso;
import org.sge.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;
    private final PeriodoAcademicoRepository periodoRepo;
    private final GradoRepository gradoRepo;
    private final SeccionRepository seccionRepo;
    private final MateriaRepository materiaRepo;
    private final DocenteRepository docenteRepo;
    private final AulaRepository aulaRepo;

    @Transactional(readOnly = true)
    public List<CursoResponse> listar(Long periodoId, Long gradoId, Long docenteId) {
        if (docenteId != null) return repository.findByDocenteId(docenteId).stream().map(this::toResponse).toList();
        if (periodoId != null && gradoId != null)
            return repository.findByPeriodoIdAndGradoId(periodoId, gradoId).stream().map(this::toResponse).toList();
        if (periodoId != null) return repository.findByPeriodoId(periodoId).stream().map(this::toResponse).toList();
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CursoResponse crear(CursoCreateRequest req) {
        PeriodoAcademico periodo = periodoRepo.findById(req.periodoId()).orElseThrow();
        var grado = gradoRepo.findById(req.gradoId()).orElseThrow();
        var seccion = seccionRepo.findById(req.seccionId()).orElseThrow();
        var materia = materiaRepo.findById(req.materiaId()).orElseThrow();

        Docente docente = null;
        Aula aula = null;
        if (req.docenteId() != null) docente = docenteRepo.findById(req.docenteId()).orElseThrow();
        if (req.aulaId() != null) aula = aulaRepo.findById(req.aulaId()).orElseThrow();

        var curso = Curso.builder()
            .periodo(periodo).grado(grado).seccion(seccion).materia(materia)
            .docente(docente).aula(aula).build();
        return toResponse(repository.save(curso));
    }

    @Transactional(readOnly = true)
    public CursoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Curso no encontrado")));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Curso no encontrado");
        repository.deleteById(id);
    }

    @Transactional
    public CursoResponse asignarDocenteAula(Long cursoId, Long docenteId, Long aulaId) {
        var curso = repository.findById(cursoId).orElseThrow();
        if (docenteId != null) {
            var docente = docenteRepo.findById(docenteId).orElseThrow();
            curso.setDocente(docente);
        }
        if (aulaId != null) {
            var aula = aulaRepo.findById(aulaId).orElseThrow();
            curso.setAula(aula);
        }
        curso.setEstado(EstadoCurso.ASIGNADO);
        return toResponse(repository.save(curso));
    }

    private CursoResponse toResponse(Curso c) {
        return new CursoResponse(
            c.getId(), c.getEstado().name(),
            c.getPeriodo().getId(), c.getPeriodo().getNombre(),
            c.getGrado().getId(), c.getGrado().getNombre(),
            c.getSeccion().getId(), c.getSeccion().getNombre(),
            c.getMateria().getId(), c.getMateria().getNombre(),
            c.getDocente() != null ? c.getDocente().getId() : null,
            c.getDocente() != null ? c.getDocente().getNombres() + " " + c.getDocente().getApellidos() : null,
            c.getAula() != null ? c.getAula().getId() : null,
            c.getAula() != null ? c.getAula().getNombre() : null
        );
    }
}
