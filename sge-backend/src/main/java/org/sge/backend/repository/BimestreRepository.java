package org.sge.backend.repository;

import org.sge.backend.model.entity.Bimestre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BimestreRepository extends JpaRepository<Bimestre, Long> {
    List<Bimestre> findByPeriodoIdOrderByNumero(Long periodoId);
}
