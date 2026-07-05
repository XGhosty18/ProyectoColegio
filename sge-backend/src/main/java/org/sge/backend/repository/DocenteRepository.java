package org.sge.backend.repository;

import org.sge.backend.model.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepository extends JpaRepository<Docente, Long> {}
