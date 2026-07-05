package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.TransicionEstadoCreateRequest;
import org.sge.backend.dto.response.TransicionEstadoResponse;
import org.sge.backend.service.TransicionEstadoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transiciones-estado")
@RequiredArgsConstructor
public class TransicionEstadoController {
    private final TransicionEstadoService service;

    @GetMapping
    public List<TransicionEstadoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public TransicionEstadoResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public TransicionEstadoResponse crear(@Valid @RequestBody TransicionEstadoCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public TransicionEstadoResponse actualizar(@PathVariable Long id, @Valid @RequestBody TransicionEstadoCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
