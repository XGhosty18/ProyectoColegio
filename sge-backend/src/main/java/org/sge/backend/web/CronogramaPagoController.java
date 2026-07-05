package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.CronogramaPagoCreateRequest;
import org.sge.backend.dto.response.CronogramaPagoResponse;
import org.sge.backend.service.CronogramaPagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cronograma-pagos")
@RequiredArgsConstructor
public class CronogramaPagoController {
    private final CronogramaPagoService service;
    @GetMapping("/alumno/{alumnoId}") public List<CronogramaPagoResponse> listar(@PathVariable Long alumnoId) { return service.listarPorAlumno(alumnoId); }

    @GetMapping("/count/pendientes")
    public long countPendientes() { return service.countPendientes(); }

    @GetMapping("/{id}")
    public CronogramaPagoResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public CronogramaPagoResponse crear(@Valid @RequestBody CronogramaPagoCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public CronogramaPagoResponse actualizar(@PathVariable Long id, @Valid @RequestBody CronogramaPagoCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
