package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AulaCreateRequest;
import org.sge.backend.dto.response.AulaResponse;
import org.sge.backend.service.AulaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService service;

    @GetMapping
    public List<AulaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AulaResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public AulaResponse crear(@Valid @RequestBody AulaCreateRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public AulaResponse actualizar(@PathVariable Long id, @Valid @RequestBody AulaCreateRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
