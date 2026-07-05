package org.sge.backend.repository;

import org.sge.backend.model.entity.TipoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoEvaluacionRepository extends JpaRepository<TipoEvaluacion, Long> {}
