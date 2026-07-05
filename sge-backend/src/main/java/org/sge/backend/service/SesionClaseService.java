package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.SesionClaseCreateRequest;
import org.sge.backend.dto.response.SesionClaseResponse;
import org.sge.backend.model.entity.SesionClase;
import org.sge.backend.repository.CursoRepository;
import org.sge.backend.repository.HorarioBloqueRepository;
import org.sge.backend.repository.SesionClaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesionClaseService {
    private final SesionClaseRepository repository;
    private final CursoRepository cursoRepo;
    private final HorarioBloqueRepository horarioRepo;

    @Transactional(readOnly = true) public List<SesionClaseResponse> listarPorCurso(Long cursoId) { return repository.findByCursoIdOrderByFecha(cursoId).stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public SesionClaseResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Sesión no encontrada")));
    }

    @Transactional
    public SesionClaseResponse actualizar(Long id, SesionClaseCreateRequest req) {
        var s = repository.findById(id).orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        var bloque = req.horarioBloqueId() != null ? horarioRepo.findById(req.horarioBloqueId()).orElseThrow() : null;
        s.setCurso(curso);
        s.setHorarioBloque(bloque);
        s.setFecha(req.fecha());
        s.setHoraInicio(req.horaInicio());
        s.setHoraFin(req.horaFin());
        s.setTema(req.tema());
        return toResponse(repository.save(s));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Sesión no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public SesionClaseResponse crear(SesionClaseCreateRequest req) {
        var curso = cursoRepo.findById(req.cursoId()).orElseThrow();
        var bloque = req.horarioBloqueId() != null ? horarioRepo.findById(req.horarioBloqueId()).orElseThrow() : null;
        var s = SesionClase.builder().curso(curso).horarioBloque(bloque)
            .fecha(req.fecha()).horaInicio(req.horaInicio()).horaFin(req.horaFin()).tema(req.tema()).build();
        return toResponse(repository.save(s));
    }

    private SesionClaseResponse toResponse(SesionClase s) {
        return new SesionClaseResponse(s.getId(), s.getFecha(), s.getHoraInicio(), s.getHoraFin(), s.getTema(), s.getEstado().name(), s.getCurso().getId(), null);
    }
}
