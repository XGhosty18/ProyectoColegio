package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.EvaluacionCreateRequest;
import org.sge.backend.model.entity.*;
import org.sge.backend.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluacionServiceTest {

    @Mock private EvaluacionRepository repository;
    @Mock private CursoRepository cursoRepo;
    @Mock private BimestreRepository bimestreRepo;
    @Mock private TipoEvaluacionRepository tipoEvalRepo;
    private EvaluacionService service;

    @BeforeEach
    void setUp() {
        service = new EvaluacionService(repository, cursoRepo, bimestreRepo, tipoEvalRepo);
    }

    @Test
    void listarPorCurso_deberiaRetornar() {
        var materia = Materia.builder().nombre("Matemáticas").build();
        materia.setId(1L);
        var curso = new Curso();
        curso.setId(1L);
        curso.setMateria(materia);
        var bimestre = Bimestre.builder().nombre("I Bimestre").build();
        bimestre.setId(1L);
        var tipo = TipoEvaluacion.builder().nombre("Examen").build();
        tipo.setId(1L);
        var e = Evaluacion.builder().nombre("Examen Final").curso(curso).bimestre(bimestre).tipoEvaluacion(tipo).build();
        when(repository.findByCursoId(1L)).thenReturn(List.of(e));

        var res = service.listarPorCurso(1L);

        assertEquals(1, res.size());
        assertEquals("Examen Final", res.getFirst().nombre());
    }

    @Test
    void crear_deberiaGuardar() {
        var materia = Materia.builder().nombre("Matemáticas").build();
        var curso = new Curso(); curso.setId(1L); curso.setMateria(materia);
        var bimestre = Bimestre.builder().nombre("I Bimestre").build();
        var tipo = TipoEvaluacion.builder().nombre("Examen").build();

        when(cursoRepo.findById(1L)).thenReturn(Optional.of(curso));
        when(bimestreRepo.findById(1L)).thenReturn(Optional.of(bimestre));
        when(tipoEvalRepo.findById(1L)).thenReturn(Optional.of(tipo));
        when(repository.save(any())).thenAnswer(inv -> {
            var e = inv.<Evaluacion>getArgument(0);
            e.setId(1L);
            return e;
        });

        var req = new EvaluacionCreateRequest(1L, 1L, 1L, "Examen Parcial", LocalDate.of(2026, 5, 15), null);
        var res = service.crear(req);

        assertEquals("Examen Parcial", res.nombre());
    }
}
