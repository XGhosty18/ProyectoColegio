package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.RolCreateRequest;
import org.sge.backend.dto.response.RolResponse;
import org.sge.backend.model.entity.Rol;
import org.sge.backend.model.entity.RolPermiso;
import org.sge.backend.repository.PermisoRepository;
import org.sge.backend.repository.RolPermisoRepository;
import org.sge.backend.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {
    private final RolRepository repository;
    private final PermisoRepository permisoRepo;
    private final RolPermisoRepository rpRepo;

    @Transactional(readOnly = true)
    public List<RolResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RolResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado")));
    }

    @Transactional
    public RolResponse crear(RolCreateRequest req) {
        var rol = Rol.builder().codigo(req.codigo()).nombre(req.nombre()).build();
        rol = repository.save(rol);
        if (req.permisoIds() != null) {
            for (var pid : req.permisoIds()) {
                var permiso = permisoRepo.findById(pid).orElseThrow();
                rpRepo.save(RolPermiso.builder().rol(rol).permiso(permiso).build());
            }
        }
        return toResponse(rol);
    }

    @Transactional
    public RolResponse actualizar(Long id, RolCreateRequest req) {
        var rol = repository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rol.setCodigo(req.codigo());
        rol.setNombre(req.nombre());
        rol = repository.save(rol);
        rpRepo.deleteAll(rol.getRolPermisos());
        rol.getRolPermisos().clear();
        if (req.permisoIds() != null) {
            for (var pid : req.permisoIds()) {
                var permiso = permisoRepo.findById(pid).orElseThrow();
                var rp = rpRepo.save(RolPermiso.builder().rol(rol).permiso(permiso).build());
                rol.getRolPermisos().add(rp);
            }
        }
        return toResponse(rol);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Rol no encontrado");
        repository.deleteById(id);
    }

    private RolResponse toResponse(Rol r) {
        var permisos = r.getRolPermisos().stream().map(rp -> rp.getPermiso().getCodigo()).toList();
        return new RolResponse(r.getId(), r.getCodigo(), r.getNombre(), permisos);
    }
}
