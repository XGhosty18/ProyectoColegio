package org.sge.backend.repository;

import org.sge.backend.model.entity.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeccionRepository extends JpaRepository<Seccion, Long> {
    List<Seccion> findByGradoId(Long gradoId);
}
