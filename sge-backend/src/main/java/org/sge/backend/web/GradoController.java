package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.GradoCreateRequest;
import org.sge.backend.dto.response.GradoResponse;
import org.sge.backend.model.enums.NivelEducativo;
import org.sge.backend.service.GradoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grados")
@RequiredArgsConstructor
public class GradoController {

    private final GradoService service;

    @GetMapping
    public List<GradoResponse> listar(@RequestParam(required = false) NivelEducativo nivel) {
        return service.listar(nivel);
    }

    @GetMapping("/{id}")
    public GradoResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public GradoResponse crear(@Valid @RequestBody GradoCreateRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public GradoResponse actualizar(@PathVariable Long id, @Valid @RequestBody GradoCreateRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
