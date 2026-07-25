package org.sge.backend.repository;

import org.sge.backend.model.entity.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByCodigoEstudiante(String codigoEstudiante);

    Optional<Alumno> findByDni(String dni);

    List<Alumno> findByEstadoActualCodigo(String estadoCodigo);

    Page<Alumno> findByEstadoActualCodigo(String estadoCodigo, Pageable pageable);
}
