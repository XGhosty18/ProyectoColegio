package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.SesionClaseCreateRequest;
import org.sge.backend.model.entity.Curso;
import org.sge.backend.model.entity.HorarioBloque;
import org.sge.backend.model.entity.Materia;
import org.sge.backend.model.entity.SesionClase;
import org.sge.backend.repository.CursoRepository;
import org.sge.backend.repository.HorarioBloqueRepository;
import org.sge.backend.repository.SesionClaseRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesionClaseServiceTest {

    @Mock private SesionClaseRepository repository;
    @Mock private CursoRepository cursoRepo;
    @Mock private HorarioBloqueRepository horarioRepo;
    private SesionClaseService service;

    @BeforeEach
    void setUp() {
        service = new SesionClaseService(repository, cursoRepo, horarioRepo);
    }

    @Test
    void listarPorCurso_deberiaRetornar() {
        var materia = Materia.builder().build();
        var curso = new Curso(); curso.setId(1L); curso.setMateria(materia);
        var s = SesionClase.builder().curso(curso).fecha(LocalDate.now()).tema("Clase 1").build();
        when(repository.findByCursoIdOrderByFecha(1L)).thenReturn(List.of(s));

        var res = service.listarPorCurso(1L);

        assertEquals(1, res.size());
    }

    @Test
    void crear_deberiaGuardar() {
        var materia = Materia.builder().build();
        var curso = new Curso(); curso.setId(1L); curso.setMateria(materia);
        when(cursoRepo.findById(1L)).thenReturn(Optional.of(curso));
        when(repository.save(any())).thenAnswer(inv -> {
            var s = inv.<SesionClase>getArgument(0);
            s.setId(1L);
            return s;
        });

        var req = new SesionClaseCreateRequest(1L, null, LocalDate.of(2026, 4, 1), LocalTime.of(8, 0), LocalTime.of(9, 30), "Introducción");
        var res = service.crear(req);

        assertEquals("Introducción", res.tema());
    }
}
