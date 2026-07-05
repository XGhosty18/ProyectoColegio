package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PadreCreateRequest;
import org.sge.backend.dto.response.PadreResponse;
import org.sge.backend.model.entity.Padre;
import org.sge.backend.repository.PadreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PadreService {
    private final PadreRepository repository;

    @Transactional(readOnly = true)
    public List<PadreResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PadreResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Padre no encontrado")));
    }

    @Transactional
    public PadreResponse crear(PadreCreateRequest req) {
        var p = new Padre();
        p.setNombres(req.nombres());
        p.setApellidos(req.apellidos());
        p.setDni(req.dni());
        p.setFechaNac(req.fechaNac());
        p.setGenero(req.genero());
        p.setTelefono(req.telefono());
        p.setDireccion(req.direccion());
        p.setParentesco(req.parentesco());
        p.setEsTitular(req.esTitular() != null ? req.esTitular() : false);
        return toResponse(repository.save(p));
    }

    @Transactional
    public PadreResponse actualizar(Long id, PadreCreateRequest req) {
        var p = repository.findById(id).orElseThrow(() -> new RuntimeException("Padre no encontrado"));
        p.setNombres(req.nombres());
        p.setApellidos(req.apellidos());
        p.setDni(req.dni());
        p.setFechaNac(req.fechaNac());
        p.setGenero(req.genero());
        p.setTelefono(req.telefono());
        p.setDireccion(req.direccion());
        p.setParentesco(req.parentesco());
        p.setEsTitular(req.esTitular() != null ? req.esTitular() : false);
        return toResponse(repository.save(p));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Padre no encontrado");
        repository.deleteById(id);
    }

    private PadreResponse toResponse(Padre p) {
        return new PadreResponse(p.getId(), p.getNombres(), p.getApellidos(), p.getDni(), p.getFechaNac(),
            p.getGenero(), p.getTelefono(), p.getDireccion(), p.getParentesco(), p.getEsTitular());
    }
}
