package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.response.AlertaFaltasResponse;
import org.sge.backend.model.entity.AlertaFaltas;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.repository.AlertaFaltasRepository;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.CursoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaFaltasServiceTest {

    @Mock private AlertaFaltasRepository repository;
    @Mock private AlumnoRepository alumnoRepo;
    @Mock private CursoRepository cursoRepo;
    private AlertaFaltasService service;

    @BeforeEach
    void setUp() {
        service = new AlertaFaltasService(repository, alumnoRepo, cursoRepo);
    }

    @Test
    void pendientes_deberiaRetornarAlertasNuevas() {
        var alumno = new Alumno();
        alumno.setNombres("Juan");
        alumno.setApellidos("Perez");
        var alerta = AlertaFaltas.builder().alumno(alumno).cantidadConsecutivas(3).nivel("ALTA").estado("NUEVA").build();
        when(repository.findByEstado("NUEVA")).thenReturn(List.of(alerta));

        var res = service.pendientes();

        assertEquals(1, res.size());
        assertEquals("NUEVA", res.getFirst().estado());
    }

    @Test
    void listarPorAlumno_deberiaRetornarAlertas() {
        var alumno = new Alumno();
        alumno.setNombres("Maria");
        alumno.setApellidos("Lopez");
        var alerta = AlertaFaltas.builder().alumno(alumno).cantidadConsecutivas(5).build();
        when(repository.findByAlumnoIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(alerta));

        var res = service.listarPorAlumno(1L);

        assertEquals(1, res.size());
    }

    @Test
    void atender_deberiaCambiarEstado() {
        var alerta = AlertaFaltas.builder().estado("NUEVA").build();
        alerta.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(alerta));

        service.atender(1L);

        assertEquals("ATENDIDA", alerta.getEstado());
        verify(repository).save(alerta);
    }

    @Test
    void atender_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.atender(99L));
    }
}
