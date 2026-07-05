package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.CronogramaPagoCreateRequest;
import org.sge.backend.dto.response.CronogramaPagoResponse;
import org.sge.backend.model.entity.CronogramaPago;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.ConceptoPagoRepository;
import org.sge.backend.repository.CronogramaPagoRepository;
import org.sge.backend.repository.PeriodoAcademicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CronogramaPagoService {
    private final CronogramaPagoRepository repository;
    private final AlumnoRepository alumnoRepo;
    private final ConceptoPagoRepository conceptoRepo;
    private final PeriodoAcademicoRepository periodoRepo;

    @Transactional(readOnly = true)
    public CronogramaPagoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Cronograma de pago no encontrado")));
    }

    public long countPendientes() { return repository.countByEstado("PENDIENTE"); }

    @Transactional(readOnly = true)
    public List<CronogramaPagoResponse> listarPorAlumno(Long alumnoId) {
        return repository.findByAlumnoIdOrderByFechaVencimiento(alumnoId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public CronogramaPagoResponse crear(CronogramaPagoCreateRequest req) {
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var concepto = conceptoRepo.findById(req.conceptoPagoId()).orElseThrow();
        var periodo = periodoRepo.findById(req.periodoId()).orElseThrow();
        var c = CronogramaPago.builder()
            .alumno(alumno).conceptoPago(concepto).periodo(periodo)
            .monto(req.monto()).fechaVencimiento(req.fechaVencimiento()).build();
        return toResponse(repository.save(c));
    }

    @Transactional
    public CronogramaPagoResponse actualizar(Long id, CronogramaPagoCreateRequest req) {
        var c = repository.findById(id).orElseThrow(() -> new RuntimeException("Cronograma de pago no encontrado"));
        var alumno = alumnoRepo.findById(req.alumnoId()).orElseThrow();
        var concepto = conceptoRepo.findById(req.conceptoPagoId()).orElseThrow();
        var periodo = periodoRepo.findById(req.periodoId()).orElseThrow();
        c.setAlumno(alumno);
        c.setConceptoPago(concepto);
        c.setPeriodo(periodo);
        c.setMonto(req.monto());
        c.setFechaVencimiento(req.fechaVencimiento());
        return toResponse(repository.save(c));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Cronograma de pago no encontrado");
        repository.deleteById(id);
    }

    private CronogramaPagoResponse toResponse(CronogramaPago c) {
        return new CronogramaPagoResponse(c.getId(), c.getAlumno().getId(), c.getAlumno().getNombres() + " " + c.getAlumno().getApellidos(),
            c.getConceptoPago().getId(), c.getConceptoPago().getNombre(), c.getMonto(), c.getFechaVencimiento(), c.getEstado());
    }
}
