package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.NotaCreateRequest;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.model.entity.Evaluacion;
import org.sge.backend.model.entity.Nota;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.EvaluacionRepository;
import org.sge.backend.repository.NotaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    @Mock private NotaRepository repository;
    @Mock private EvaluacionRepository evaluacionRepo;
    @Mock private AlumnoRepository alumnoRepo;
    private NotaService service;

    @BeforeEach
    void setUp() {
        service = new NotaService(repository, evaluacionRepo, alumnoRepo);
    }

    @Test
    void listarPorEvaluacion_deberiaRetornar() {
        var eval = new Evaluacion(); eval.setId(1L); eval.setNombre("Examen");
        var alumno = new Alumno(); alumno.setNombres("Juan"); alumno.setApellidos("Perez");
        var nota = Nota.builder().evaluacion(eval).alumno(alumno).valor(BigDecimal.valueOf(15)).build();
        when(repository.findByEvaluacionId(1L)).thenReturn(List.of(nota));

        var res = service.listarPorEvaluacion(1L);

        assertEquals(1, res.size());
        assertEquals(BigDecimal.valueOf(15), res.getFirst().valor());
    }

    @Test
    void crear_deberiaGuardar() {
        var eval = new Evaluacion(); eval.setId(1L); eval.setNombre("Examen");
        var alumno = new Alumno(); alumno.setId(1L); alumno.setNombres("Juan"); alumno.setApellidos("Perez");
        when(evaluacionRepo.findById(1L)).thenReturn(Optional.of(eval));
        when(alumnoRepo.findById(1L)).thenReturn(Optional.of(alumno));
        when(repository.save(any())).thenAnswer(inv -> {
            var n = inv.<Nota>getArgument(0);
            n.setId(1L);
            return n;
        });

        var req = new NotaCreateRequest(1L, 1L, BigDecimal.valueOf(18), "Buen trabajo");
        var res = service.crear(req);

        assertEquals(BigDecimal.valueOf(18), res.valor());
    }
}
