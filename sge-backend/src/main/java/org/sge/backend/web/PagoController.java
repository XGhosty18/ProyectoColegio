package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PagoCreateRequest;
import org.sge.backend.dto.response.PagoResponse;
import org.sge.backend.service.PagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService service;
    @GetMapping("/alumno/{alumnoId}") public List<PagoResponse> listar(@PathVariable Long alumnoId) { return service.listarPorAlumno(alumnoId); }

    @GetMapping("/{id}")
    public PagoResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public PagoResponse crear(@Valid @RequestBody PagoCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public PagoResponse actualizar(@PathVariable Long id, @Valid @RequestBody PagoCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
