package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.TipoEvaluacionCreateRequest;
import org.sge.backend.dto.response.TipoEvaluacionResponse;
import org.sge.backend.service.TipoEvaluacionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-evaluacion")
@RequiredArgsConstructor
public class TipoEvaluacionController {
    private final TipoEvaluacionService service;
    @GetMapping public List<TipoEvaluacionResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public TipoEvaluacionResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public TipoEvaluacionResponse crear(@Valid @RequestBody TipoEvaluacionCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public TipoEvaluacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody TipoEvaluacionCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
