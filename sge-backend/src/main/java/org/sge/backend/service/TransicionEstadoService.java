package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.TransicionEstadoCreateRequest;
import org.sge.backend.dto.response.TransicionEstadoResponse;
import org.sge.backend.model.entity.TransicionEstado;
import org.sge.backend.repository.EstadoAlumnoRepository;
import org.sge.backend.repository.TransicionEstadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransicionEstadoService {
    private final TransicionEstadoRepository repository;
    private final EstadoAlumnoRepository estadoRepo;

    @Transactional(readOnly = true)
    public List<TransicionEstadoResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TransicionEstadoResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Transición no encontrada")));
    }

    @Transactional
    public TransicionEstadoResponse crear(TransicionEstadoCreateRequest req) {
        var origen = estadoRepo.findById(req.estadoOrigenId()).orElseThrow();
        var destino = estadoRepo.findById(req.estadoDestinoId()).orElseThrow();
        var t = TransicionEstado.builder()
            .estadoOrigen(origen).estadoDestino(destino)
            .codigoGatillante(req.codigoGatillante())
            .esAutomatica(req.esAutomatica() != null ? req.esAutomatica() : false)
            .requiereAdmin(req.requiereAdmin() != null ? req.requiereAdmin() : true)
            .requiereConsejo(req.requiereConsejo() != null ? req.requiereConsejo() : false)
            .notificaPadre(req.notificaPadre() != null ? req.notificaPadre() : false)
            .build();
        return toResponse(repository.save(t));
    }

    @Transactional
    public TransicionEstadoResponse actualizar(Long id, TransicionEstadoCreateRequest req) {
        var t = repository.findById(id).orElseThrow(() -> new RuntimeException("Transición no encontrada"));
        var origen = estadoRepo.findById(req.estadoOrigenId()).orElseThrow();
        var destino = estadoRepo.findById(req.estadoDestinoId()).orElseThrow();
        t.setEstadoOrigen(origen);
        t.setEstadoDestino(destino);
        t.setCodigoGatillante(req.codigoGatillante());
        t.setEsAutomatica(req.esAutomatica() != null ? req.esAutomatica() : false);
        t.setRequiereAdmin(req.requiereAdmin() != null ? req.requiereAdmin() : true);
        t.setRequiereConsejo(req.requiereConsejo() != null ? req.requiereConsejo() : false);
        t.setNotificaPadre(req.notificaPadre() != null ? req.notificaPadre() : false);
        return toResponse(repository.save(t));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Transición no encontrada");
        repository.deleteById(id);
    }

    private TransicionEstadoResponse toResponse(TransicionEstado t) {
        return new TransicionEstadoResponse(t.getId(),
            t.getEstadoOrigen().getId(), t.getEstadoOrigen().getNombre(),
            t.getEstadoDestino().getId(), t.getEstadoDestino().getNombre(),
            t.getCodigoGatillante(), t.getEsAutomatica(),
            t.getRequiereAdmin(), t.getRequiereConsejo(), t.getNotificaPadre());
    }
}
