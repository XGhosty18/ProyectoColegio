package org.sge.backend.repository;

import org.sge.backend.model.entity.AlertaFaltas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaFaltasRepository extends JpaRepository<AlertaFaltas, Long> {
    List<AlertaFaltas> findByAlumnoIdOrderByCreatedAtDesc(Long alumnoId);
    List<AlertaFaltas> findByEstado(String estado);
}
