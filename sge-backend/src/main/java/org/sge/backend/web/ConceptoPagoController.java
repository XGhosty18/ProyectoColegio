package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.ConceptoPagoCreateRequest;
import org.sge.backend.dto.response.ConceptoPagoResponse;
import org.sge.backend.service.ConceptoPagoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conceptos-pago")
@RequiredArgsConstructor
public class ConceptoPagoController {
    private final ConceptoPagoService service;
    @GetMapping public List<ConceptoPagoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ConceptoPagoResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public ConceptoPagoResponse crear(@Valid @RequestBody ConceptoPagoCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public ConceptoPagoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ConceptoPagoCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
