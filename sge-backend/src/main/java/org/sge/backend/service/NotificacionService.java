package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.NotificacionCreateRequest;
import org.sge.backend.dto.response.NotificacionResponse;
import org.sge.backend.model.entity.Notificacion;
import org.sge.backend.repository.NotificacionRepository;
import org.sge.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {
    private final NotificacionRepository repository;
    private final UsuarioRepository usuarioRepo;

    @Transactional(readOnly = true)
    public NotificacionResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Notificación no encontrada")));
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> pendientes(Long usuarioId) {
        return repository.findByLeidaFalseAndUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public NotificacionResponse crear(NotificacionCreateRequest req) {
        var usuario = usuarioRepo.findById(req.usuarioId()).orElseThrow();
        var n = Notificacion.builder()
            .usuario(usuario).titulo(req.titulo()).cuerpo(req.cuerpo())
            .tipo(req.tipo() != null ? req.tipo() : "INFO")
            .entidadTipo(req.entidadTipo()).entidadId(req.entidadId()).build();
        return toResponse(repository.save(n));
    }

    @Transactional
    public NotificacionResponse actualizar(Long id, NotificacionCreateRequest req) {
        var n = repository.findById(id).orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        var usuario = usuarioRepo.findById(req.usuarioId()).orElseThrow();
        n.setUsuario(usuario);
        n.setTitulo(req.titulo());
        n.setCuerpo(req.cuerpo());
        n.setTipo(req.tipo() != null ? req.tipo() : "INFO");
        n.setEntidadTipo(req.entidadTipo());
        n.setEntidadId(req.entidadId());
        return toResponse(repository.save(n));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Notificación no encontrada");
        repository.deleteById(id);
    }

    @Transactional
    public void marcarLeida(Long id) {
        var n = repository.findById(id).orElseThrow();
        n.setLeida(true);
        repository.save(n);
    }

    private NotificacionResponse toResponse(Notificacion n) {
        return new NotificacionResponse(n.getId(), n.getTitulo(), n.getCuerpo(), n.getUsuario().getId(),
            n.getTipo(), n.getLeida(), n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
    }
}
