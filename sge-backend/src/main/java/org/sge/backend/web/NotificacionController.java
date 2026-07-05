package org.sge.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.NotificacionCreateRequest;
import org.sge.backend.dto.response.NotificacionResponse;
import org.sge.backend.service.NotificacionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    private final NotificacionService service;
    @GetMapping("/usuario/{usuarioId}") public List<NotificacionResponse> listar(@PathVariable Long usuarioId) { return service.listarPorUsuario(usuarioId); }
    @GetMapping("/pendientes/{usuarioId}") public List<NotificacionResponse> pendientes(@PathVariable Long usuarioId) { return service.pendientes(usuarioId); }

    @GetMapping("/{id}")
    public NotificacionResponse obtener(@PathVariable Long id) { return service.obtenerPorId(id); }

    @PostMapping
    public NotificacionResponse crear(@Valid @RequestBody NotificacionCreateRequest req) { return service.crear(req); }

    @PutMapping("/{id}")
    public NotificacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody NotificacionCreateRequest req) { return service.actualizar(id, req); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }

    @PostMapping("/{id}/leer") public void marcarLeida(@PathVariable Long id) { service.marcarLeida(id); }
}
