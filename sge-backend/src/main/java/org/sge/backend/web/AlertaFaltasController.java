package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AlertaFaltasCreateRequest;
import org.sge.backend.dto.response.AlertaFaltasResponse;
import org.sge.backend.service.AlertaFaltasService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alertas-faltas")
@RequiredArgsConstructor
public class AlertaFaltasController {
    private final AlertaFaltasService service;
    @GetMapping("/pendientes") public List<AlertaFaltasResponse> pendientes() { return service.pendientes(); }
    @GetMapping("/alumno/{alumnoId}") public List<AlertaFaltasResponse> porAlumno(@PathVariable Long alumnoId) { return service.listarPorAlumno(alumnoId); }

    @GetMapping("/{id}")
    public AlertaFaltasResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public AlertaFaltasResponse crear(@Valid @RequestBody AlertaFaltasCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public AlertaFaltasResponse actualizar(@PathVariable Long id, @Valid @RequestBody AlertaFaltasCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }

    @PostMapping("/{id}/atender") public void atender(@PathVariable Long id) { service.atender(id); }
}
