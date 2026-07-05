package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.BimestreUpdateRequest;
import org.sge.backend.model.entity.Bimestre;
import org.sge.backend.model.entity.PeriodoAcademico;
import org.sge.backend.model.enums.EstadoBimestre;
import org.sge.backend.repository.BimestreRepository;
import org.sge.backend.repository.PeriodoAcademicoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BimestreServiceTest {

    @Mock private BimestreRepository repository;
    @Mock private PeriodoAcademicoRepository periodoRepo;
    private BimestreService service;

    @BeforeEach
    void setUp() {
        service = new BimestreService(repository, periodoRepo);
    }

    @Test
    void listarPorPeriodo_deberiaRetornar() {
        var periodo = new PeriodoAcademico(); periodo.setId(1L);
        var b = Bimestre.builder().periodo(periodo).numero(1).nombre("I Bimestre")
            .fechaInicio(LocalDate.of(2026, 3, 1)).fechaFin(LocalDate.of(2026, 5, 30)).build();
        when(repository.findByPeriodoIdOrderByNumero(1L)).thenReturn(List.of(b));

        var res = service.listarPorPeriodo(1L);

        assertEquals(1, res.size());
        assertEquals("I Bimestre", res.getFirst().nombre());
    }

    @Test
    void actualizar_deberiaModificar() {
        var periodo = new PeriodoAcademico(); periodo.setId(1L);
        var b = Bimestre.builder().periodo(periodo).numero(1).nombre("I Bimestre").estado(EstadoBimestre.ABIERTO).build();
        b.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(b));
        when(repository.save(b)).thenReturn(b);

        var req = new BimestreUpdateRequest(LocalDate.of(2026, 3, 5), LocalDate.of(2026, 5, 28), EstadoBimestre.CERRADO);
        var res = service.actualizar(1L, req);

        assertEquals(LocalDate.of(2026, 3, 5), res.fechaInicio());
    }
}
