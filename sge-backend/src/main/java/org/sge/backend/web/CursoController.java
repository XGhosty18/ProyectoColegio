package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.CursoCreateRequest;
import org.sge.backend.dto.response.CursoResponse;
import org.sge.backend.service.CursoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService service;

    @GetMapping
    public List<CursoResponse> listar(
            @RequestParam(required = false) Long periodoId,
            @RequestParam(required = false) Long gradoId,
            @RequestParam(required = false) Long docenteId) {
        return service.listar(periodoId, gradoId, docenteId);
    }

    @GetMapping("/{id}")
    public CursoResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public CursoResponse crear(@Valid @RequestBody CursoCreateRequest request) {
        return service.crear(request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PutMapping("/{id}/asignar-recursos")
    public CursoResponse asignarRecursos(
            @PathVariable Long id,
            @RequestParam(required = false) Long docenteId,
            @RequestParam(required = false) Long aulaId) {
        return service.asignarDocenteAula(id, docenteId, aulaId);
    }
}
