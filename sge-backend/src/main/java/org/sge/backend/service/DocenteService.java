package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.DocenteCreateRequest;
import org.sge.backend.dto.response.DocenteResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Docente;
import org.sge.backend.repository.DocenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final DocenteRepository repository;

    @Transactional(readOnly = true)
    public List<DocenteResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DocenteResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Docente no encontrado")));
    }

    @Transactional
    public DocenteResponse crear(DocenteCreateRequest req) {
        var docente = new Docente();
        docente.setCodigoEmpleado(req.codigoEmpleado());
        docente.setEspecialidad(req.especialidad());
        docente.setTipoContrato(req.tipoContrato());
        docente.setCargaHorariaMax(req.cargaHorariaMax() != null ? req.cargaHorariaMax() : 40);
        docente.setNombres(req.nombres());
        docente.setApellidos(req.apellidos());
        docente.setDni(req.dni());
        docente.setFechaNac(req.fechaNac());
        docente.setGenero(req.genero());
        docente.setTelefono(req.telefono());
        docente.setDireccion(req.direccion());
        return toResponse(repository.save(docente));
    }

    @Transactional
    public DocenteResponse actualizar(Long id, DocenteCreateRequest req) {
        var d = repository.findById(id).orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        d.setNombres(req.nombres());
        d.setApellidos(req.apellidos());
        d.setDni(req.dni());
        d.setFechaNac(req.fechaNac());
        d.setGenero(req.genero());
        d.setTelefono(req.telefono());
        d.setDireccion(req.direccion());
        d.setCodigoEmpleado(req.codigoEmpleado());
        d.setEspecialidad(req.especialidad());
        d.setTipoContrato(req.tipoContrato());
        d.setCargaHorariaMax(req.cargaHorariaMax() != null ? req.cargaHorariaMax() : 40);
        return toResponse(repository.save(d));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Docente no encontrado");
        repository.deleteById(id);
    }

    private DocenteResponse toResponse(Docente d) {
        return new DocenteResponse(
            d.getId(), d.getNombres(), d.getApellidos(), d.getDni(), d.getFechaNac(),
            d.getGenero(), d.getTelefono(), d.getDireccion(),
            d.getCodigoEmpleado(), d.getEspecialidad(), d.getTipoContrato(), d.getCargaHorariaMax()
        );
    }
}
