package org.sge.backend.repository;

import org.sge.backend.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByCronogramaPagoId(Long cronogramaPagoId);
    List<Pago> findByAlumnoIdOrderByFechaPagoDesc(Long alumnoId);
}
