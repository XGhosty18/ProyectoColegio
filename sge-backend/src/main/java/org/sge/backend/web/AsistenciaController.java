package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AsistenciaCreateRequest;
import org.sge.backend.dto.response.AsistenciaResponse;
import org.sge.backend.service.AsistenciaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService service;

    @GetMapping("/sesion/{sesionId}")
    public List<AsistenciaResponse> listarPorSesion(@PathVariable Long sesionId) {
        return service.listarPorSesion(sesionId);
    }

    @PostMapping
    public AsistenciaResponse crear(@Valid @RequestBody AsistenciaCreateRequest request) {
        return service.crear(request);
    }

    @GetMapping("/{id}")
    public AsistenciaResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public AsistenciaResponse actualizar(@PathVariable Long id, @Valid @RequestBody AsistenciaCreateRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PostMapping("/masivo")
    public AsistenciaResponse crearMasivo(@Valid @RequestBody List<AsistenciaCreateRequest> requests) {
        return service.crearMasivo(requests);
    }
}
