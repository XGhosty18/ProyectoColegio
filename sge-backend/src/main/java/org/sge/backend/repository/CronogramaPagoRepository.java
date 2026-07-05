package org.sge.backend.repository;

import org.sge.backend.model.entity.CronogramaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CronogramaPagoRepository extends JpaRepository<CronogramaPago, Long> {
    List<CronogramaPago> findByAlumnoIdOrderByFechaVencimiento(Long alumnoId);
    List<CronogramaPago> findByConceptoPagoId(Long conceptoPagoId);
    long countByEstado(String estado);
}
