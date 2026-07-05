package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.MateriaCreateRequest;
import org.sge.backend.dto.response.MateriaResponse;
import org.sge.backend.service.MateriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService service;

    @GetMapping
    public List<MateriaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public MateriaResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public MateriaResponse crear(@Valid @RequestBody MateriaCreateRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public MateriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MateriaCreateRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
