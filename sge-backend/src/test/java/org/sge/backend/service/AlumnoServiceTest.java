package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.AlumnoCreateRequest;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.model.entity.EstadoAlumno;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.EstadoAlumnoRepository;
import org.sge.backend.repository.PadreRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock private AlumnoRepository repository;
    @Mock private EstadoAlumnoRepository estadoRepo;
    @Mock private PadreRepository padreRepo;
    private AlumnoService service;

    @BeforeEach
    void setUp() {
        service = new AlumnoService(repository, estadoRepo, padreRepo);
    }

    @Test
    void crear_deberiaGuardar() {
        var req = new AlumnoCreateRequest("Juan", "Perez", "12345678", LocalDate.of(2010, 5, 10),
            "M", "999999999", "Av. Siempre Viva", "AL001", null);
        when(repository.findByCodigoEstudiante("AL001")).thenReturn(Optional.empty());
        when(repository.findByDni("12345678")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            var a = inv.<Alumno>getArgument(0);
            a.setId(1L);
            return a;
        });

        var res = service.crear(req);

        assertEquals("AL001", res.codigoEstudiante());
        verify(repository).save(any());
    }

    @Test
    void crear_codigoDuplicado_deberiaLanzarExcepcion() {
        var req = new AlumnoCreateRequest("Juan", "Perez", "12345678", LocalDate.of(2010, 5, 10),
            "M", "999999999", "Av. Siempre Viva", "AL001", null);
        when(repository.findByCodigoEstudiante("AL001")).thenReturn(Optional.of(new Alumno()));

        assertThrows(BusinessRuleViolationException.class, () -> service.crear(req));
    }

    @Test
    void crear_dniDuplicado_deberiaLanzarExcepcion() {
        var req = new AlumnoCreateRequest("Juan", "Perez", "12345678", LocalDate.of(2010, 5, 10),
            "M", "999999999", "Av. Siempre Viva", "AL001", null);
        when(repository.findByCodigoEstudiante("AL001")).thenReturn(Optional.empty());
        when(repository.findByDni("12345678")).thenReturn(Optional.of(new Alumno()));

        assertThrows(BusinessRuleViolationException.class, () -> service.crear(req));
    }

    @Test
    void listar_sinFiltro_deberiaRetornarTodos() {
        var a = new Alumno();
        a.setNombres("Juan");
        when(repository.findAll()).thenReturn(List.of(a));

        var res = service.listar(null);

        assertEquals(1, res.size());
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornar() {
        var a = new Alumno();
        a.setId(1L);
        a.setCodigoEstudiante("AL001");
        when(repository.findById(1L)).thenReturn(Optional.of(a));

        var res = service.obtenerPorId(1L);

        assertEquals("AL001", res.codigoEstudiante());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
    }
}
