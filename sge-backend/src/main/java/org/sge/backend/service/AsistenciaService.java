package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AsistenciaCreateRequest;
import org.sge.backend.dto.response.AsistenciaResponse;
import org.sge.backend.model.entity.AsistenciaAlumno;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.AsistenciaAlumnoRepository;
import org.sge.backend.repository.SesionClaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaAlumnoRepository repository;
    private final SesionClaseRepository sesionRepo;
    private final AlumnoRepository alumnoRepo;

    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listarPorSesion(Long sesionId) {
        return repository.findBySesionId(sesionId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AsistenciaResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Asistencia no encontrada")));
    }

    @Transactional
    public AsistenciaResponse actualizar(Long id, AsistenciaCreateRequest req) {
        var a = repository.findById(id).orElseThrow(() -> new RuntimeException("Asistencia no encontrada"));
        var sesion = sesionRepo.findById(req.sesionId()).orElseThrow();
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        a.setSesion(sesion);
        a.setAlumno(alumno);
        a.setTipoAsistencia(req.tipoAsistencia());
        a.setMinutosTardanza(req.minutosTardanza());
        a.setObservacion(req.observacion());
        return toResponse(repository.save(a));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Asistencia no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public AsistenciaResponse crear(AsistenciaCreateRequest req) {
        var sesion = sesionRepo.findById(req.sesionId()).orElseThrow();
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var asistencia = AsistenciaAlumno.builder()
            .sesion(sesion).alumno(alumno)
            .tipoAsistencia(req.tipoAsistencia())
            .minutosTardanza(req.minutosTardanza())
            .observacion(req.observacion()).build();
        return toResponse(repository.save(asistencia));
    }

    @Transactional
    public AsistenciaResponse crearMasivo(List<AsistenciaCreateRequest> requests) {
        var first = requests.get(0);
        var sesion = sesionRepo.findById(first.sesionId()).orElseThrow();
        for (var req : requests) {
            var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
            repository.save(AsistenciaAlumno.builder()
                .sesion(sesion).alumno(alumno)
                .tipoAsistencia(req.tipoAsistencia())
                .minutosTardanza(req.minutosTardanza())
                .observacion(req.observacion()).build());
        }
        return new AsistenciaResponse(null, null, null, null, null, null, "Carga masiva exitosa: " + requests.size() + " registros");
    }

    private AsistenciaResponse toResponse(AsistenciaAlumno a) {
        return new AsistenciaResponse(
            a.getId(), a.getTipoAsistencia(), a.getMinutosTardanza(), a.getObservacion(),
            a.getSesion().getId(), a.getAlumno().getId(),
            a.getAlumno().getNombres() + " " + a.getAlumno().getApellidos()
        );
    }
}
