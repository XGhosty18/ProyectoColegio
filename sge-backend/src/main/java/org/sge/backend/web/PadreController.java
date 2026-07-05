package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PadreCreateRequest;
import org.sge.backend.dto.response.PadreResponse;
import org.sge.backend.service.PadreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/padres")
@RequiredArgsConstructor
public class PadreController {
    private final PadreService service;

    @GetMapping
    public List<PadreResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public PadreResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public PadreResponse crear(@Valid @RequestBody PadreCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public PadreResponse actualizar(@PathVariable Long id, @Valid @RequestBody PadreCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
