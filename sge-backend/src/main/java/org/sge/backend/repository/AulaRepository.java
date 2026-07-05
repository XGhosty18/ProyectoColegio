package org.sge.backend.repository;

import org.sge.backend.model.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    Optional<Aula> findByCodigo(String codigo);
}
