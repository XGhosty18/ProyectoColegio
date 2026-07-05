package org.sge.backend.repository;

import org.sge.backend.model.entity.HistorialEstadoAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoAlumnoRepository extends JpaRepository<HistorialEstadoAlumno, Long> {

    List<HistorialEstadoAlumno> findByAlumnoIdOrderByFechaCambioDesc(Long alumnoId);
}
