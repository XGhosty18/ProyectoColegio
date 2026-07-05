package org.sge.backend.repository;

import org.sge.backend.model.entity.AsistenciaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsistenciaAlumnoRepository extends JpaRepository<AsistenciaAlumno, Long> {

    List<AsistenciaAlumno> findBySesionId(Long sesionId);

    @Query(value = """
        WITH faltas_ordenadas AS (
            SELECT a.alumno_id, s.fecha,
                   ROW_NUMBER() OVER (PARTITION BY a.alumno_id ORDER BY s.fecha) as rn,
                   s.fecha - ROW_NUMBER() OVER (PARTITION BY a.alumno_id ORDER BY s.fecha) AS grp
            FROM asistencias_alumno a
            JOIN sesiones_clase s ON s.id = a.sesion_id
            WHERE a.alumno_id = :alumnoId
            AND s.curso_id = :cursoId
            AND a.tipo_asistencia = 'FALTA'
            AND s.fecha <= :fechaRef
        )
        SELECT COUNT(*) FROM faltas_ordenadas
        GROUP BY grp
        ORDER BY MAX(fecha) DESC
        LIMIT 1
        """, nativeQuery = true)
    Integer findFaltasConsecutivas(Long alumnoId, Long cursoId, LocalDate fechaRef);
}
