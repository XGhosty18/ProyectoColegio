package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.CambioEstadoRequest;
import org.sge.backend.dto.response.HistorialEstadoResponse;
import org.sge.backend.model.entity.EstadoAlumno;
import org.sge.backend.service.EstadoAlumnoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estados-alumno")
@RequiredArgsConstructor
public class EstadoAlumnoController {

    private final EstadoAlumnoService service;

    @GetMapping
    public List<EstadoAlumno> listar() { return service.listar(); }

    @GetMapping("/{alumnoId}/historial")
    public List<HistorialEstadoResponse> historial(@PathVariable Long alumnoId) {
        return service.historial(alumnoId);
    }

    @PostMapping("/transicion")
    public HistorialEstadoResponse transicion(@Valid @RequestBody CambioEstadoRequest request) {
        return service.cambiarEstado(request);
    }
}
