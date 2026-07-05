package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AulaCreateRequest;
import org.sge.backend.dto.response.AulaResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Aula;
import org.sge.backend.repository.AulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AulaService {

    private final AulaRepository repository;

    @Transactional(readOnly = true)
    public List<AulaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AulaResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Aula no encontrada")));
    }

    @Transactional
    public AulaResponse actualizar(Long id, AulaCreateRequest req) {
        var a = repository.findById(id).orElseThrow(() -> new RuntimeException("Aula no encontrada"));
        a.setNombre(req.nombre());
        a.setCodigo(req.codigo());
        a.setCapacidad(req.capacidad());
        a.setTipo(req.tipo());
        return toResponse(repository.save(a));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Aula no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public AulaResponse crear(AulaCreateRequest req) {
        if (repository.findByCodigo(req.codigo()).isPresent())
            throw new BusinessRuleViolationException("CODIGO_DUPLICADO", "Código de aula ya existe");
        return toResponse(repository.save(Aula.builder().nombre(req.nombre()).codigo(req.codigo()).capacidad(req.capacidad()).tipo(req.tipo()).build()));
    }

    private AulaResponse toResponse(Aula a) {
        return new AulaResponse(a.getId(), a.getNombre(), a.getCodigo(), a.getCapacidad(), a.getTipo());
    }
}
