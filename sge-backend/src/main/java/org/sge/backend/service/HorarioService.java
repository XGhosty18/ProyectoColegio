package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.HorarioBloqueCreateRequest;
import org.sge.backend.dto.response.HorarioBloqueResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.*;
import org.sge.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioBloqueRepository repository;
    private final CursoRepository cursoRepo;
    private final AulaRepository aulaRepo;

    @Transactional(readOnly = true)
    public List<HorarioBloqueResponse> listarPorCurso(Long cursoId) {
        return repository.findByCursoId(cursoId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioBloqueResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public HorarioBloqueResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Bloque horario no encontrado")));
    }

    @Transactional
    public HorarioBloqueResponse actualizar(Long id, HorarioBloqueCreateRequest req) {
        var bloque = repository.findById(id).orElseThrow(() -> new RuntimeException("Bloque horario no encontrado"));
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        Aula aula = null;
        if (req.aulaId() != null) aula = aulaRepo.findById(req.aulaId()).orElseThrow();

        validarColision(id, req.diaSemana(), req.horaInicio(), req.horaFin(),
            aula != null ? aula.getId() : null,
            curso.getDocente() != null ? curso.getDocente().getId() : null,
            curso.getGrado().getId(), curso.getSeccion().getId());

        bloque.setCurso(curso);
        bloque.setAula(aula);
        bloque.setDiaSemana(req.diaSemana());
        bloque.setHoraInicio(req.horaInicio());
        bloque.setHoraFin(req.horaFin());
        return toResponse(repository.save(bloque));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Bloque horario no encontrado");
        repository.deleteById(id);
    }

    @Transactional
    public HorarioBloqueResponse crear(HorarioBloqueCreateRequest req) {
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        Aula aula = null;
        if (req.aulaId() != null) aula = aulaRepo.findById(req.aulaId()).orElseThrow();

        validarColision(null, req.diaSemana(), req.horaInicio(), req.horaFin(),
            aula != null ? aula.getId() : null,
            curso.getDocente() != null ? curso.getDocente().getId() : null,
            curso.getGrado().getId(), curso.getSeccion().getId());

        var bloque = HorarioBloque.builder()
            .curso(curso).aula(aula)
            .diaSemana(req.diaSemana()).horaInicio(req.horaInicio()).horaFin(req.horaFin())
            .build();
        return toResponse(repository.save(bloque));
    }

    private void validarColision(Long excludeId, Integer dia, java.time.LocalTime ini, java.time.LocalTime fin,
                                  Long aulaId, Long docenteId, Long gradoId, Long seccionId) {
        if (!repository.findConflictos(
                excludeId != null ? excludeId : -1, dia, ini, fin,
                aulaId != null ? aulaId : -1,
                docenteId != null ? docenteId : -1,
                gradoId, seccionId).isEmpty()) {
            throw new BusinessRuleViolationException("COLISION_HORARIO",
                "El horario seleccionado colisiona con otro bloque existente");
        }
    }

    private HorarioBloqueResponse toResponse(HorarioBloque hb) {
        var curso = hb.getCurso();
        return new HorarioBloqueResponse(
            hb.getId(), hb.getDiaSemana(), hb.getHoraInicio(), hb.getHoraFin(),
            curso.getId(), curso.getMateria().getNombre(),
            hb.getAula() != null ? hb.getAula().getId() : null,
            hb.getAula() != null ? hb.getAula().getNombre() : null,
            curso.getDocente() != null ? curso.getDocente().getId() : null,
            curso.getDocente() != null ? curso.getDocente().getNombres() + " " + curso.getDocente().getApellidos() : null,
            curso.getMateria().getNombre(), curso.getMateria().getId()
        );
    }
}
