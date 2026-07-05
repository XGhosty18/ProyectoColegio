package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.CambioEstadoRequest;
import org.sge.backend.dto.response.HistorialEstadoResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.EstadoAlumno;
import org.sge.backend.model.entity.HistorialEstadoAlumno;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.EstadoAlumnoRepository;
import org.sge.backend.repository.HistorialEstadoAlumnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoAlumnoService {

    private final AlumnoRepository alumnoRepo;
    private final EstadoAlumnoRepository estadoRepo;
    private final HistorialEstadoAlumnoRepository historialRepo;

    @Transactional(readOnly = true)
    public List<EstadoAlumno> listar() { return estadoRepo.findAll(); }

    @Transactional(readOnly = true)
    public List<HistorialEstadoResponse> historial(Long alumnoId) {
        return historialRepo.findByAlumnoIdOrderByFechaCambioDesc(alumnoId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public HistorialEstadoResponse cambiarEstado(CambioEstadoRequest req) {
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var destino = estadoRepo.findByCodigo(req.estadoCodigo()).orElseThrow();

        if (alumno.getEstadoActual() != null) {
            var origen = alumno.getEstadoActual();
            boolean transicionValida = origen.getTransicionesOrigen().stream()
                .anyMatch(t -> t.getEstadoDestino().getCodigo().equals(req.estadoCodigo()));
            if (!transicionValida)
                throw new BusinessRuleViolationException("TRANSICION_INVALIDA",
                    "No se puede cambiar de " + origen.getCodigo() + " a " + req.estadoCodigo());
        }

        var anterior = alumno.getEstadoActual();
        alumno.setEstadoActual(destino);
        alumno.setSubEstado(req.motivo());
        alumno.setFechaUltimoEstado(Instant.now());
        alumnoRepo.save(alumno);

        var historial = HistorialEstadoAlumno.builder()
            .alumno(alumno).estadoAnterior(anterior).estadoNuevo(destino)
            .motivo(req.motivo() != null ? req.motivo() : "Cambio de estado automático")
            .documentoUrl(req.referenciaDocumento())
            .fechaCambio(Instant.now()).build();
        return toResponse(historialRepo.save(historial));
    }

    private HistorialEstadoResponse toResponse(HistorialEstadoAlumno h) {
        return new HistorialEstadoResponse(
            h.getId(),
            h.getEstadoAnterior() != null ? h.getEstadoAnterior().getCodigo() : null,
            h.getEstadoNuevo().getCodigo(),
            h.getEstadoNuevo().getNombre(),
            h.getMotivo(), h.getFechaCambio(),
            "SYSTEM", h.getDocumentoUrl()
        );
    }
}
