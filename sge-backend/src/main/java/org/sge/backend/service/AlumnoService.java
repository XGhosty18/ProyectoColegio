package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AlumnoCreateRequest;
import org.sge.backend.dto.response.AlumnoResponse;
import org.sge.backend.dto.response.PadreResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.EstadoAlumnoRepository;
import org.sge.backend.repository.PadreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final AlumnoRepository repository;
    private final EstadoAlumnoRepository estadoRepo;
    private final PadreRepository padreRepo;

    @Transactional(readOnly = true)
    public List<AlumnoResponse> listar(String estadoCodigo) {
        if (estadoCodigo != null && !estadoCodigo.isBlank()) {
            return repository.findByEstadoActualCodigo(estadoCodigo).stream()
                .map(this::toResponse).toList();
        }
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<AlumnoResponse> listarPaginado(String estadoCodigo, Pageable pageable) {
        if (estadoCodigo != null && !estadoCodigo.isBlank()) {
            return repository.findByEstadoActualCodigo(estadoCodigo, pageable).map(this::toResponse);
        }
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AlumnoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Alumno no encontrado")));
    }

    @Transactional
    public AlumnoResponse crear(AlumnoCreateRequest req) {
        if (repository.findByCodigoEstudiante(req.codigoEstudiante()).isPresent())
            throw new BusinessRuleViolationException("CODIGO_DUPLICADO", "Código de estudiante ya existe");
        if (repository.findByDni(req.dni()).isPresent())
            throw new BusinessRuleViolationException("DNI_DUPLICADO", "DNI ya registrado");

        var estado = req.estadoActualId() != null ? estadoRepo.findById(req.estadoActualId()).orElseThrow(() -> new BusinessRuleViolationException("ESTADO_INVALIDO", "Estado actual no encontrado")) : null;
        var alumno = new Alumno(req.codigoEstudiante(), estado, null, req.fechaNac(), null, new java.util.HashSet<>());
        alumno.setNombres(req.nombres());
        alumno.setApellidos(req.apellidos());
        alumno.setDni(req.dni());
        alumno.setFechaNac(req.fechaNac());
        alumno.setGenero(req.genero());
        alumno.setTelefono(req.telefono());
        alumno.setDireccion(req.direccion());
        return toResponse(repository.save(alumno));
    }

    @Transactional
    public AlumnoResponse actualizar(Long id, AlumnoCreateRequest req) {
        var a = repository.findById(id).orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        a.setNombres(req.nombres());
        a.setApellidos(req.apellidos());
        a.setDni(req.dni());
        a.setFechaNac(req.fechaNac());
        a.setGenero(req.genero());
        a.setTelefono(req.telefono());
        a.setDireccion(req.direccion());
        if (req.estadoActualId() != null) {
            var estado = estadoRepo.findById(req.estadoActualId()).orElseThrow(() -> new BusinessRuleViolationException("ESTADO_INVALIDO", "Estado actual no encontrado"));
            a.setEstadoActual(estado);
        }
        return toResponse(repository.save(a));
    }

    @Transactional(readOnly = true)
    public List<PadreResponse> listarPadres(Long alumnoId) {
        var a = repository.findById(alumnoId).orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        return a.getPadres().stream().map(p -> new PadreResponse(p.getId(), p.getNombres(), p.getApellidos(), p.getDni(), p.getFechaNac(),
            p.getGenero(), p.getTelefono(), p.getDireccion(), p.getParentesco(), p.getEsTitular())).toList();
    }

    @Transactional
    public void asignarPadre(Long alumnoId, Long padreId) {
        var a = repository.findById(alumnoId).orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        var p = padreRepo.findById(padreId).orElseThrow(() -> new RuntimeException("Padre no encontrado"));
        a.getPadres().add(p);
        repository.save(a);
    }

    @Transactional
    public void desasignarPadre(Long alumnoId, Long padreId) {
        var a = repository.findById(alumnoId).orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        a.getPadres().removeIf(p -> p.getId().equals(padreId));
        repository.save(a);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Alumno no encontrado");
        repository.deleteById(id);
    }

    private AlumnoResponse toResponse(Alumno a) {
        return new AlumnoResponse(
            a.getId(), a.getNombres(), a.getApellidos(), a.getDni(), a.getFechaNac(),
            a.getGenero(), a.getTelefono(), a.getDireccion(),
            a.getCodigoEstudiante(),
            a.getEstadoActual() != null ? a.getEstadoActual().getId() : null,
            a.getEstadoActual() != null ? a.getEstadoActual().getCodigo() : null,
            a.getEstadoActual() != null ? a.getEstadoActual().getNombre() : null,
            a.getSubEstado(), a.getFechaIngreso()
        );
    }
}
