package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.AlumnoCreateRequest;
import org.sge.backend.dto.response.AlumnoResponse;
import org.sge.backend.dto.response.PadreResponse;
import org.sge.backend.service.AlumnoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService service;

    @GetMapping
    public List<AlumnoResponse> listar(@RequestParam(required = false) String estado) {
        return service.listar(estado);
    }

    @GetMapping("/paginado")
    public Page<AlumnoResponse> listarPaginado(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return service.listarPaginado(estado, PageRequest.of(page, size, Sort.by("id").ascending()));
    }

    @GetMapping("/{id}")
    public AlumnoResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public AlumnoResponse crear(@Valid @RequestBody AlumnoCreateRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public AlumnoResponse actualizar(@PathVariable Long id, @Valid @RequestBody AlumnoCreateRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/{id}/padres")
    public List<PadreResponse> listarPadres(@PathVariable Long id) { return service.listarPadres(id); }

    @PostMapping("/{id}/padres")
    public void asignarPadre(@PathVariable Long id, @RequestBody java.util.Map<String, Long> body) {
        service.asignarPadre(id, body.get("padreId"));
    }

    @DeleteMapping("/{id}/padres/{padreId}")
    public void desasignarPadre(@PathVariable Long id, @PathVariable Long padreId) {
        service.desasignarPadre(id, padreId);
    }
}
