package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.SesionClaseCreateRequest;
import org.sge.backend.dto.response.SesionClaseResponse;
import org.sge.backend.service.SesionClaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sesiones")
@RequiredArgsConstructor
public class SesionClaseController {
    private final SesionClaseService service;
    @GetMapping("/curso/{cursoId}") public List<SesionClaseResponse> listar(@PathVariable Long cursoId) { return service.listarPorCurso(cursoId); }

    @GetMapping("/{id}")
    public SesionClaseResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping public SesionClaseResponse crear(@Valid @RequestBody SesionClaseCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public SesionClaseResponse actualizar(@PathVariable Long id, @Valid @RequestBody SesionClaseCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
