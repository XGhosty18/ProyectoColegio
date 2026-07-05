package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.SeccionCreateRequest;
import org.sge.backend.dto.response.SeccionResponse;
import org.sge.backend.service.SeccionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/secciones")
@RequiredArgsConstructor
public class SeccionController {
    private final SeccionService service;

    @GetMapping
    public List<SeccionResponse> listar(@RequestParam(required = false) Long gradoId) {
        return service.listar(gradoId);
    }

    @GetMapping("/{id}")
    public SeccionResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public SeccionResponse crear(@Valid @RequestBody SeccionCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public SeccionResponse actualizar(@PathVariable Long id, @Valid @RequestBody SeccionCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
