package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.BimestreCreateRequest;
import org.sge.backend.dto.request.BimestreUpdateRequest;
import org.sge.backend.dto.response.BimestreResponse;
import org.sge.backend.service.BimestreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bimestres")
@RequiredArgsConstructor
public class BimestreController {
    private final BimestreService service;
    @GetMapping public List<BimestreResponse> listar(@RequestParam Long periodoId) { return service.listarPorPeriodo(periodoId); }

    @GetMapping("/{id}")
    public BimestreResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public BimestreResponse crear(@Valid @RequestBody BimestreCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}") public BimestreResponse actualizar(@PathVariable Long id, @Valid @RequestBody BimestreUpdateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
