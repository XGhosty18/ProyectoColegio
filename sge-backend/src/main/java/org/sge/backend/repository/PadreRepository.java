package org.sge.backend.repository;

import org.sge.backend.model.entity.Padre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PadreRepository extends JpaRepository<Padre, Long> {
    Optional<Padre> findByDni(String dni);
}
