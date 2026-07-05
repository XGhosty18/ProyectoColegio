package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PagoCreateRequest;
import org.sge.backend.dto.response.PagoResponse;
import org.sge.backend.model.entity.Pago;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.CronogramaPagoRepository;
import org.sge.backend.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository repository;
    private final AlumnoRepository alumnoRepo;
    private final CronogramaPagoRepository cronogramaRepo;

    @Transactional(readOnly = true)
    public PagoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado")));
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorAlumno(Long alumnoId) {
        return repository.findByAlumnoIdOrderByFechaPagoDesc(alumnoId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public PagoResponse crear(PagoCreateRequest req) {
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var cronograma = cronogramaRepo.findById(req.cronogramaPagoId()).orElseThrow();
        var pago = Pago.builder()
            .alumno(alumno).cronogramaPago(cronograma)
            .monto(req.monto()).metodo(req.metodo()).referencia(req.referencia())
            .fechaPago(req.fechaPago() != null ? req.fechaPago() : java.time.LocalDate.now())
            .build();
        return toResponse(repository.save(pago));
    }

    @Transactional
    public PagoResponse actualizar(Long id, PagoCreateRequest req) {
        var p = repository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var cronograma = cronogramaRepo.findById(req.cronogramaPagoId()).orElseThrow();
        p.setAlumno(alumno);
        p.setCronogramaPago(cronograma);
        p.setMonto(req.monto());
        p.setMetodo(req.metodo());
        p.setReferencia(req.referencia());
        p.setFechaPago(req.fechaPago() != null ? req.fechaPago() : java.time.LocalDate.now());
        return toResponse(repository.save(p));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Pago no encontrado");
        repository.deleteById(id);
    }

    private PagoResponse toResponse(Pago p) {
        return new PagoResponse(p.getId(), p.getAlumno().getId(), p.getAlumno().getNombres() + " " + p.getAlumno().getApellidos(),
            p.getCronogramaPago().getId(), p.getMonto(), p.getMetodo(), p.getReferencia(), p.getFechaPago());
    }
}
