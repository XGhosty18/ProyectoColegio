package org.sge.backend.repository;

import org.sge.backend.model.entity.PeriodoAcademico;
import org.sge.backend.model.enums.EstadoPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Long> {

    Optional<PeriodoAcademico> findByCodigo(String codigo);

    List<PeriodoAcademico> findByEstadoOrderByFechaInicioDesc(EstadoPeriodo estado);

    Optional<PeriodoAcademico> findByEstado(EstadoPeriodo estado);
}
