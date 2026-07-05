package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.CambioEstadoRequest;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.model.entity.EstadoAlumno;
import org.sge.backend.model.entity.HistorialEstadoAlumno;
import org.sge.backend.model.entity.TransicionEstado;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.EstadoAlumnoRepository;
import org.sge.backend.repository.HistorialEstadoAlumnoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadoAlumnoServiceTest {

    @Mock private AlumnoRepository alumnoRepo;
    @Mock private EstadoAlumnoRepository estadoRepo;
    @Mock private HistorialEstadoAlumnoRepository historialRepo;
    private EstadoAlumnoService service;

    @BeforeEach
    void setUp() {
        service = new EstadoAlumnoService(alumnoRepo, estadoRepo, historialRepo);
    }

    @Test
    void historial_deberiaRetornarRegistros() {
        when(historialRepo.findByAlumnoIdOrderByFechaCambioDesc(1L)).thenReturn(List.of());
        var res = service.historial(1L);
        assertTrue(res.isEmpty());
    }

    @Test
    void cambiarEstado_transicionValida_deberiaCambiar() {
        var activo = EstadoAlumno.builder().codigo("ACTIVO").build();
        var retirado = EstadoAlumno.builder().codigo("RETIRADO").build();
        var transicion = new TransicionEstado();
        transicion.setEstadoDestino(retirado);
        activo.setTransicionesOrigen(List.of(transicion));

        var alumno = new Alumno();
        alumno.setId(1L);
        alumno.setEstadoActual(activo);

        when(alumnoRepo.findById(1L)).thenReturn(Optional.of(alumno));
        when(estadoRepo.findByCodigo("RETIRADO")).thenReturn(Optional.of(retirado));
        when(historialRepo.save(any())).thenAnswer(inv -> {
            var h = inv.<HistorialEstadoAlumno>getArgument(0);
            h.setId(1L);
            return h;
        });

        var req = new CambioEstadoRequest(1L, "RETIRADO", "Retiro voluntario", null);
        var res = service.cambiarEstado(req);

        assertEquals("RETIRADO", res.estadoDestinoCodigo());
    }

    @Test
    void cambiarEstado_transicionInvalida_deberiaLanzarExcepcion() {
        var activo = EstadoAlumno.builder().codigo("ACTIVO").build();
        activo.setTransicionesOrigen(List.of());

        var alumno = new Alumno();
        alumno.setEstadoActual(activo);

        when(alumnoRepo.findById(1L)).thenReturn(Optional.of(alumno));
        when(estadoRepo.findByCodigo("EXPULSADO")).thenReturn(Optional.of(EstadoAlumno.builder().codigo("EXPULSADO").build()));

        var req = new CambioEstadoRequest(1L, "EXPULSADO", "Falta grave", null);
        assertThrows(BusinessRuleViolationException.class, () -> service.cambiarEstado(req));
    }
}
