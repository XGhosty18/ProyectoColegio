package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PermisoCreateRequest;
import org.sge.backend.dto.response.PermisoResponse;
import org.sge.backend.service.PermisoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permisos")
@RequiredArgsConstructor
public class PermisoController {
    private final PermisoService service;

    @GetMapping
    public List<PermisoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public PermisoResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public PermisoResponse crear(@Valid @RequestBody PermisoCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public PermisoResponse actualizar(@PathVariable Long id, @Valid @RequestBody PermisoCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
