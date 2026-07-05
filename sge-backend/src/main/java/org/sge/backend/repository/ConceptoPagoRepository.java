package org.sge.backend.repository;

import org.sge.backend.model.entity.ConceptoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConceptoPagoRepository extends JpaRepository<ConceptoPago, Long> {
    List<ConceptoPago> findByGradoId(Long gradoId);
}
