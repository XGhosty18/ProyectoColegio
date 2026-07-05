package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.HorarioBloqueCreateRequest;
import org.sge.backend.dto.response.HorarioBloqueResponse;
import org.sge.backend.service.HorarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService service;

    @GetMapping
    public List<HorarioBloqueResponse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/curso/{cursoId}")
    public List<HorarioBloqueResponse> listarPorCurso(@PathVariable Long cursoId) {
        return service.listarPorCurso(cursoId);
    }

    @GetMapping("/{id}")
    public HorarioBloqueResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public HorarioBloqueResponse crear(@Valid @RequestBody HorarioBloqueCreateRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public HorarioBloqueResponse actualizar(@PathVariable Long id, @Valid @RequestBody HorarioBloqueCreateRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
