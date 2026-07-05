package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.NotaCreateRequest;
import org.sge.backend.dto.response.NotaResponse;
import org.sge.backend.service.NotaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notas")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService service;

    @GetMapping("/evaluacion/{evaluacionId}")
    public List<NotaResponse> listarPorEvaluacion(@PathVariable Long evaluacionId) {
        return service.listarPorEvaluacion(evaluacionId);
    }

    @GetMapping("/alumno/{alumnoId}/bimestre/{bimestreId}")
    public List<NotaResponse> listarPorAlumno(@PathVariable Long alumnoId, @PathVariable Long bimestreId) {
        return service.listarPorAlumnoYBimestre(alumnoId, bimestreId);
    }

    @GetMapping("/{id}")
    public NotaResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public NotaResponse crear(@Valid @RequestBody NotaCreateRequest request) {
        return service.crear(request);
    }

    @PostMapping("/masivo")
    public void crearMasivo(@Valid @RequestBody List<NotaCreateRequest> requests) {
        service.crearMasivo(requests);
    }

    @PutMapping("/{id}")
    public NotaResponse actualizar(@PathVariable Long id, @Valid @RequestBody NotaCreateRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
