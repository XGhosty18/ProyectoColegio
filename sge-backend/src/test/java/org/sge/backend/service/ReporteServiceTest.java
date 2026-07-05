package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.AsistenciaAlumnoRepository;
import org.sge.backend.repository.BimestreRepository;
import org.sge.backend.repository.CursoRepository;
import org.sge.backend.repository.EvaluacionRepository;
import org.sge.backend.repository.NotaRepository;

import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock private AlumnoRepository alumnoRepo;
    @Mock private CursoRepository cursoRepo;
    @Mock private NotaRepository notaRepo;
    @Mock private AsistenciaAlumnoRepository asistenciaRepo;
    @Mock private EvaluacionRepository evaluacionRepo;
    @Mock private BimestreRepository bimestreRepo;
    private ReporteService service;

    @BeforeEach
    void setUp() {
        service = new ReporteService(alumnoRepo, cursoRepo, notaRepo, asistenciaRepo, evaluacionRepo, bimestreRepo);
    }

    @Test
    void generarReporteNotas_deberiaRetornarBytes() throws Exception {
        when(cursoRepo.findAll()).thenReturn(Collections.emptyList());
        var bytes = service.generarReporteNotas(null, null);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }
}
