package org.sge.backend.repository;

import org.sge.backend.model.entity.Grado;
import org.sge.backend.model.enums.NivelEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradoRepository extends JpaRepository<Grado, Long> {

    List<Grado> findByNivelOrderByOrden(NivelEducativo nivel);

    List<Grado> findAllByOrderByOrden();
}
