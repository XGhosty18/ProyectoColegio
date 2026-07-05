package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.HorarioBloqueCreateRequest;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.*;
import org.sge.backend.repository.AulaRepository;
import org.sge.backend.repository.CursoRepository;
import org.sge.backend.repository.HorarioBloqueRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    @Mock private HorarioBloqueRepository repository;
    @Mock private CursoRepository cursoRepo;
    @Mock private AulaRepository aulaRepo;
    private HorarioService service;

    @BeforeEach
    void setUp() {
        service = new HorarioService(repository, cursoRepo, aulaRepo);
    }

    @Test
    void listarPorCurso_deberiaRetornar() {
        when(repository.findByCursoId(1L)).thenReturn(List.of());
        assertTrue(service.listarPorCurso(1L).isEmpty());
    }

    @Test
    void listarTodos_deberiaRetornar() {
        when(repository.findAll()).thenReturn(List.of());
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    void crear_sinColision_deberiaGuardar() {
        var materia = Materia.builder().nombre("Matemáticas").build();
        materia.setId(1L);
        var grado = Grado.builder().nombre("1° Primaria").build();
        grado.setId(1L);
        var seccion = new Seccion(); seccion.setId(1L); seccion.setNombre("A");
        var curso = Curso.builder().periodo(null).grado(grado).seccion(seccion).materia(materia).build();
        curso.setId(1L);

        when(cursoRepo.findById(1L)).thenReturn(Optional.of(curso));
        when(repository.findConflictos(any(), anyInt(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of());

        var req = new HorarioBloqueCreateRequest(1L, null, 1, LocalTime.of(8, 0), LocalTime.of(9, 30));
        when(repository.save(any())).thenAnswer(inv -> {
            var h = inv.<HorarioBloque>getArgument(0);
            h.setId(1L);
            return h;
        });

        var res = service.crear(req);
        assertNotNull(res.id());
    }

    @Test
    void crear_conColision_deberiaLanzarExcepcion() {
        var materia = Materia.builder().build();
        materia.setId(1L);
        var grado = Grado.builder().build();
        grado.setId(1L);
        var seccion = new Seccion();
        seccion.setId(1L);
        var curso = Curso.builder().periodo(null).grado(grado).seccion(seccion).materia(materia).build();
        curso.setId(1L);

        when(cursoRepo.findById(1L)).thenReturn(Optional.of(curso));
        when(repository.findConflictos(any(), anyInt(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(new HorarioBloque()));

        var req = new HorarioBloqueCreateRequest(1L, null, 1, LocalTime.of(8, 0), LocalTime.of(9, 30));
        assertThrows(BusinessRuleViolationException.class, () -> service.crear(req));
    }
}
