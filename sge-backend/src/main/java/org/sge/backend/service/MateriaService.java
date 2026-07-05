package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.MateriaCreateRequest;
import org.sge.backend.dto.response.MateriaResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Materia;
import org.sge.backend.repository.MateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository repository;

    @Transactional(readOnly = true)
    public List<MateriaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MateriaResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Materia no encontrada")));
    }

    @Transactional
    public MateriaResponse actualizar(Long id, MateriaCreateRequest req) {
        var m = repository.findById(id).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        m.setNombre(req.nombre());
        m.setCodigo(req.codigo());
        m.setHorasSemanalesReq(req.horasSemanalesReq());
        m.setTipo(req.tipo());
        return toResponse(repository.save(m));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Materia no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public MateriaResponse crear(MateriaCreateRequest req) {
        if (repository.findByCodigo(req.codigo()).isPresent())
            throw new BusinessRuleViolationException("CODIGO_DUPLICADO", "Código de materia ya existe");
        return toResponse(repository.save(Materia.builder().nombre(req.nombre()).codigo(req.codigo()).horasSemanalesReq(req.horasSemanalesReq()).tipo(req.tipo()).build()));
    }

    private MateriaResponse toResponse(Materia m) {
        return new MateriaResponse(m.getId(), m.getNombre(), m.getCodigo(), m.getHorasSemanalesReq(), m.getTipo());
    }
}
