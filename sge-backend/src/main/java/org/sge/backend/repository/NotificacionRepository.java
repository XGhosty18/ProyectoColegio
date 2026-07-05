package org.sge.backend.repository;

import org.sge.backend.model.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    List<Notificacion> findByLeidaFalseAndUsuarioId(Long usuarioId);
}
