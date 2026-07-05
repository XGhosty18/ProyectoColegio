package org.sge.backend.repository;

import org.sge.backend.model.entity.SesionClase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SesionClaseRepository extends JpaRepository<SesionClase, Long> {
    List<SesionClase> findByCursoIdOrderByFecha(Long cursoId);
}
