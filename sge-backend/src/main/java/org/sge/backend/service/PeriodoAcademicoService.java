package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PeriodoCreateRequest;
import org.sge.backend.dto.response.PeriodoResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.exception.ResourceNotFoundException;
import org.sge.backend.model.entity.PeriodoAcademico;
import org.sge.backend.model.enums.EstadoPeriodo;
import org.sge.backend.repository.PeriodoAcademicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeriodoAcademicoService {

    private final PeriodoAcademicoRepository repository;

    @Transactional(readOnly = true)
    public List<PeriodoResponse> listar(String estado) {
        if (estado != null && !estado.isBlank()) {
            return repository.findByEstadoOrderByFechaInicioDesc(EstadoPeriodo.valueOf(estado.toUpperCase()))
                .stream().map(this::toResponse).toList();
        }
        return repository.findAll().stream()
            .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PeriodoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Periodo", "id", id)));
    }

    @Transactional(readOnly = true)
    public PeriodoResponse obtenerActivo() {
        return toResponse(repository.findByEstado(EstadoPeriodo.ACTIVO)
            .orElseThrow(() -> new ResourceNotFoundException("Periodo", "estado", "ACTIVO")));
    }

    @Transactional
    public PeriodoResponse crear(PeriodoCreateRequest request) {
        if (repository.findByCodigo(request.codigo()).isPresent()) {
            throw new BusinessRuleViolationException("CODIGO_DUPLICADO",
                "Ya existe un periodo con el código " + request.codigo());
        }
        PeriodoAcademico periodo = PeriodoAcademico.builder()
            .nombre(request.nombre())
            .codigo(request.codigo())
            .fechaInicio(request.fechaInicio())
            .fechaFin(request.fechaFin())
            .build();
        return toResponse(repository.save(periodo));
    }

    @Transactional
    public PeriodoResponse activarPlan(Long id) {
        PeriodoAcademico periodo = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Periodo", "id", id));
        if (periodo.getEstado() != EstadoPeriodo.PLANIFICACION) {
            throw new BusinessRuleViolationException("ESTADO_INVALIDO",
                "Solo periodos en PLANIFICACION pueden activarse");
        }
        periodo.setEstado(EstadoPeriodo.ACTIVO);
        return toResponse(repository.save(periodo));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Periodo", "id", id);
        repository.deleteById(id);
    }

    private PeriodoResponse toResponse(PeriodoAcademico p) {
        return new PeriodoResponse(
            p.getId(), p.getNombre(), p.getCodigo(),
            p.getFechaInicio(), p.getFechaFin(),
            p.getEstado().name());
    }
}
