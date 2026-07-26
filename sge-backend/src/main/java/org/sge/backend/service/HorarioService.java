package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.HorarioBloqueCreateRequest;
import org.sge.backend.dto.response.HorarioBloqueResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.*;
import org.sge.backend.repository.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioBloqueRepository repository;
    private final CursoRepository cursoRepo;
    private final AulaRepository aulaRepo;

    private final Map<String, String> jobStatuses = new ConcurrentHashMap<>();

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

    public String iniciarGeneracionAsincrona(Long periodoId) {
        String jobId = UUID.randomUUID().toString();
        jobStatuses.put(jobId, "EN_PROCESO");
        generarHorariosProceso(jobId, periodoId);
        return jobId;
    }

    public String obtenerEstadoTrabajo(String jobId) {
        return jobStatuses.getOrDefault(jobId, "NO_ENCONTRADO");
    }

    @Async
    public void generarHorariosProceso(String jobId, Long periodoId) {
        try {
            List<Curso> cursos = cursoRepo.findByPeriodoId(periodoId);
            List<Aula> aulas = aulaRepo.findAll();

            LocalTime[] bloquesInicio = {
                LocalTime.of(8, 0), LocalTime.of(8, 45), LocalTime.of(9, 30),
                LocalTime.of(10, 30), LocalTime.of(11, 15), LocalTime.of(12, 0)
            };
            LocalTime[] bloquesFin = {
                LocalTime.of(8, 45), LocalTime.of(9, 30), LocalTime.of(10, 15),
                LocalTime.of(11, 15), LocalTime.of(12, 0), LocalTime.of(12, 45)
            };

            for (Curso curso : cursos) {
                int horasReq = curso.getMateria() != null && curso.getMateria().getHorasSemanalesReq() != null
                    ? curso.getMateria().getHorasSemanalesReq() : 2;

                int horasAsignadas = 0;
                for (int dia = 1; dia <= 5 && horasAsignadas < horasReq; dia++) {
                    for (int b = 0; b < bloquesInicio.length && horasAsignadas < horasReq; b++) {
                        LocalTime ini = bloquesInicio[b];
                        LocalTime fin = bloquesFin[b];

                        Aula aulaAsignada = curso.getAula();
                        if (aulaAsignada == null && !aulas.isEmpty()) {
                            aulaAsignada = aulas.get(0);
                        }

                        Long docenteId = curso.getDocente() != null ? curso.getDocente().getId() : null;
                        Long aulaId = aulaAsignada != null ? aulaAsignada.getId() : null;

                        try {
                            validarColision(null, dia, ini, fin, aulaId, docenteId,
                                curso.getGrado().getId(), curso.getSeccion().getId());

                            HorarioBloque hb = HorarioBloque.builder()
                                .curso(curso)
                                .aula(aulaAsignada)
                                .diaSemana(dia)
                                .horaInicio(ini)
                                .horaFin(fin)
                                .build();
                            repository.save(hb);
                            horasAsignadas++;
                        } catch (BusinessRuleViolationException ignored) {
                            // Slot has conflict, try next slot
                        }
                    }
                }
            }
            jobStatuses.put(jobId, "COMPLETADO");
        } catch (Exception e) {
            jobStatuses.put(jobId, "ERROR: " + e.getMessage());
        }
    }

    private void validarColision(Long excludeId, Integer dia, LocalTime ini, LocalTime fin,
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
