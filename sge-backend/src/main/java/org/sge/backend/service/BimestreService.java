package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.BimestreCreateRequest;
import org.sge.backend.dto.request.BimestreUpdateRequest;
import org.sge.backend.dto.response.BimestreResponse;
import org.sge.backend.model.entity.Bimestre;
import org.sge.backend.model.enums.EstadoBimestre;
import org.sge.backend.repository.BimestreRepository;
import org.sge.backend.repository.PeriodoAcademicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BimestreService {
    private final BimestreRepository repository;
    private final PeriodoAcademicoRepository periodoRepo;

    @Transactional(readOnly = true) public List<BimestreResponse> listarPorPeriodo(Long periodoId) { return repository.findByPeriodoIdOrderByNumero(periodoId).stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public BimestreResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Bimestre no encontrado")));
    }

    @Transactional
    public BimestreResponse crear(BimestreCreateRequest req) {
        var periodo = periodoRepo.findById(req.periodoId()).orElseThrow();
        var b = Bimestre.builder()
            .periodo(periodo).numero(req.numero()).nombre(req.nombre())
            .fechaInicio(req.fechaInicio()).fechaFin(req.fechaFin())
            .estado(req.estado() != null ? req.estado() : EstadoBimestre.ABIERTO).build();
        return toResponse(repository.save(b));
    }

    @Transactional
    public BimestreResponse actualizar(Long id, BimestreUpdateRequest req) {
        var b = repository.findById(id).orElseThrow();
        b.setFechaInicio(req.fechaInicio());
        b.setFechaFin(req.fechaFin());
        if (req.estado() != null) b.setEstado(req.estado());
        return toResponse(repository.save(b));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Bimestre no encontrado");
        repository.deleteById(id);
    }

    private BimestreResponse toResponse(Bimestre b) {
        return new BimestreResponse(b.getId(), b.getNumero(), b.getNombre(), b.getFechaInicio(), b.getFechaFin(), b.getEstado().name(), b.getPeriodo().getId());
    }
}
