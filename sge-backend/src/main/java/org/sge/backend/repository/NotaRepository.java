package org.sge.backend.repository;

import org.sge.backend.model.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

    List<Nota> findByEvaluacionId(Long evaluacionId);

    List<Nota> findByAlumnoIdAndEvaluacionBimestreId(Long alumnoId, Long bimestreId);

    @Query("SELECT AVG(n.valor) FROM Nota n WHERE n.alumno.id = :alumnoId AND n.evaluacion.bimestre.id = :bimestreId")
    Double findPromedioByAlumnoAndBimestre(Long alumnoId, Long bimestreId);
}
