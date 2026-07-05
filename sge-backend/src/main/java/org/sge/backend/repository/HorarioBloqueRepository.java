package org.sge.backend.repository;

import org.sge.backend.model.entity.HorarioBloque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioBloqueRepository extends JpaRepository<HorarioBloque, Long> {

    List<HorarioBloque> findByCursoId(Long cursoId);

    @Query("""
        SELECT hb FROM HorarioBloque hb
        WHERE hb.id != :excludeBloqueId
        AND hb.diaSemana = :dia
        AND hb.horaInicio < :horaFin
        AND hb.horaFin > :horaInicio
        AND (hb.aula.id = :aulaId OR hb.curso.docente.id = :docenteId
             OR hb.curso.grado.id = :gradoId AND hb.curso.seccion.id = :seccionId)
        """)
    List<HorarioBloque> findConflictos(Long excludeBloqueId, Integer dia, LocalTime horaInicio, LocalTime horaFin,
                                       Long aulaId, Long docenteId, Long gradoId, Long seccionId);
}
