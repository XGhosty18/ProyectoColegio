package org.sge.backend.repository;

import org.sge.backend.model.entity.TransicionEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransicionEstadoRepository extends JpaRepository<TransicionEstado, Long> {
    List<TransicionEstado> findByEstadoOrigenId(Long estadoOrigenId);
}
