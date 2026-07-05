package org.sge.backend.repository;

import org.sge.backend.model.entity.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByCursoId(Long cursoId);
    List<Evaluacion> findByBimestreId(Long bimestreId);
}
