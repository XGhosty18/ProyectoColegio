package org.sge.backend.repository;

import org.sge.backend.model.entity.EstadoAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoAlumnoRepository extends JpaRepository<EstadoAlumno, Long> {

    Optional<EstadoAlumno> findByCodigo(String codigo);
}
