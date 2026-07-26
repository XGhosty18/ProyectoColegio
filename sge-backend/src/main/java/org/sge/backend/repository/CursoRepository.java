package org.sge.backend.repository;

import org.sge.backend.model.entity.Curso;
import org.sge.backend.model.enums.EstadoCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByPeriodoIdAndEstadoIn(Long periodoId, List<EstadoCurso> estados);

    List<Curso> findByPeriodoId(Long periodoId);

    List<Curso> findByPeriodoIdAndGradoId(Long periodoId, Long gradoId);

    List<Curso> findByDocenteId(Long docenteId);

    @Query("SELECT COALESCE(SUM(m.horasSemanalesReq), 0) FROM Curso c JOIN c.materia m WHERE c.docente.id = :docenteId AND c.periodo.id = :periodoId")
    Integer findCargaHorariaDocente(Long docenteId, Long periodoId);
}
