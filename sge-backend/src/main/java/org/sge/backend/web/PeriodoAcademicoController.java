package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.PeriodoCreateRequest;
import org.sge.backend.dto.response.PeriodoResponse;
import org.sge.backend.service.PeriodoAcademicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/periodos")
@RequiredArgsConstructor
public class PeriodoAcademicoController {

    private final PeriodoAcademicoService service;

    @GetMapping
    public List<PeriodoResponse> listar(@RequestParam(required = false) String estado) {
        return service.listar(estado);
    }

    @GetMapping("/activo")
    public PeriodoResponse obtenerActivo() {
        return service.obtenerActivo();
    }

    @GetMapping("/{id}")
    public PeriodoResponse obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public PeriodoResponse crear(@Valid @RequestBody PeriodoCreateRequest request) {
        return service.crear(request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PutMapping("/{id}/activar-plan")
    public PeriodoResponse activarPlan(@PathVariable Long id) {
        return service.activarPlan(id);
    }
}
