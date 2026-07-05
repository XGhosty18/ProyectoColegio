package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.PeriodoCreateRequest;
import org.sge.backend.model.entity.PeriodoAcademico;
import org.sge.backend.model.enums.EstadoPeriodo;
import org.sge.backend.repository.PeriodoAcademicoRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeriodoAcademicoServiceTest {

    @Mock
    private PeriodoAcademicoRepository repository;

    private PeriodoAcademicoService service;

    @BeforeEach
    void setUp() {
        service = new PeriodoAcademicoService(repository);
    }

    @Test
    void crearPeriodo_deberiaRetornarResponse() {
        var req = new PeriodoCreateRequest("2026", "P-2026", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 12, 20));
        when(repository.save(any())).thenAnswer(inv -> {
            var p = inv.<PeriodoAcademico>getArgument(0);
            p.setId(1L);
            return p;
        });

        var res = service.crear(req);

        assertEquals("2026", res.nombre());
        assertEquals(EstadoPeriodo.PLANIFICACION.name(), res.estado());
        verify(repository).save(any());
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarResponse() {
        var entity = PeriodoAcademico.builder().nombre("2026").codigo("P-2026")
            .fechaInicio(LocalDate.of(2026, 3, 1)).fechaFin(LocalDate.of(2026, 12, 20))
            .build();
        entity.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var res = service.obtenerPorId(1L);

        assertEquals(1L, res.id());
        assertEquals("2026", res.nombre());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
    }
}
