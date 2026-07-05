package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.ConceptoPagoCreateRequest;
import org.sge.backend.dto.response.ConceptoPagoResponse;
import org.sge.backend.model.entity.ConceptoPago;
import org.sge.backend.repository.ConceptoPagoRepository;
import org.sge.backend.repository.GradoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConceptoPagoService {
    private final ConceptoPagoRepository repository;
    private final GradoRepository gradoRepo;

    @Transactional(readOnly = true)
    public ConceptoPagoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Concepto de pago no encontrado")));
    }

    @Transactional(readOnly = true)
    public List<ConceptoPagoResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ConceptoPagoResponse crear(ConceptoPagoCreateRequest req) {
        var grado = req.gradoId() != null ? gradoRepo.findById(req.gradoId()).orElseThrow() : null;
        var c = ConceptoPago.builder()
            .nombre(req.nombre()).montoBase(req.montoBase())
            .periodicidad(req.periodicidad()).grado(grado).build();
        return toResponse(repository.save(c));
    }

    @Transactional
    public ConceptoPagoResponse actualizar(Long id, ConceptoPagoCreateRequest req) {
        var c = repository.findById(id).orElseThrow(() -> new RuntimeException("Concepto de pago no encontrado"));
        var grado = req.gradoId() != null ? gradoRepo.findById(req.gradoId()).orElseThrow() : null;
        c.setNombre(req.nombre());
        c.setMontoBase(req.montoBase());
        c.setPeriodicidad(req.periodicidad());
        c.setGrado(grado);
        return toResponse(repository.save(c));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Concepto de pago no encontrado");
        repository.deleteById(id);
    }

    private ConceptoPagoResponse toResponse(ConceptoPago c) {
        return new ConceptoPagoResponse(c.getId(), c.getNombre(), c.getMontoBase(), c.getPeriodicidad(), c.getGrado() != null ? c.getGrado().getId() : null);
    }
}
