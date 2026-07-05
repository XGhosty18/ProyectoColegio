package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.RolCreateRequest;
import org.sge.backend.dto.response.RolResponse;
import org.sge.backend.service.RolService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolController {
    private final RolService service;

    @GetMapping
    public List<RolResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public RolResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public RolResponse crear(@Valid @RequestBody RolCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public RolResponse actualizar(@PathVariable Long id, @Valid @RequestBody RolCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
