package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.EvaluacionCreateRequest;
import org.sge.backend.dto.response.EvaluacionResponse;
import org.sge.backend.service.EvaluacionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {
    private final EvaluacionService service;
    @GetMapping("/curso/{cursoId}") public List<EvaluacionResponse> listar(@PathVariable Long cursoId) { return service.listarPorCurso(cursoId); }

    @GetMapping("/{id}")
    public EvaluacionResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping public EvaluacionResponse crear(@Valid @RequestBody EvaluacionCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public EvaluacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody EvaluacionCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
