package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.CursoCreateRequest;
import org.sge.backend.model.entity.*;
import org.sge.backend.model.enums.EstadoCurso;
import org.sge.backend.repository.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock private CursoRepository repository;
    @Mock private PeriodoAcademicoRepository periodoRepo;
    @Mock private GradoRepository gradoRepo;
    @Mock private SeccionRepository seccionRepo;
    @Mock private MateriaRepository materiaRepo;
    @Mock private DocenteRepository docenteRepo;
    @Mock private AulaRepository aulaRepo;
    private CursoService service;

    @BeforeEach
    void setUp() {
        service = new CursoService(repository, periodoRepo, gradoRepo, seccionRepo,
            materiaRepo, docenteRepo, aulaRepo);
    }

    @Test
    void crear_deberiaGuardar() {
        var periodo = PeriodoAcademico.builder().nombre("2026").build();
        periodo.setId(1L);
        var grado = Grado.builder().nombre("1° Primaria").build();
        var seccion = new Seccion(); seccion.setNombre("A");
        var materia = Materia.builder().nombre("Matemáticas").build();

        when(periodoRepo.findById(1L)).thenReturn(Optional.of(periodo));
        when(gradoRepo.findById(1L)).thenReturn(Optional.of(grado));
        when(seccionRepo.findById(1L)).thenReturn(Optional.of(seccion));
        when(materiaRepo.findById(1L)).thenReturn(Optional.of(materia));
        when(repository.save(any())).thenAnswer(inv -> {
            var c = inv.<Curso>getArgument(0);
            c.setId(1L);
            return c;
        });

        var req = new CursoCreateRequest(1L, 1L, 1L, 1L, null, null);
        var res = service.crear(req);

        assertNotNull(res.id());
        assertEquals(EstadoCurso.BORRADOR.name(), res.estado());
    }

    @Test
    void asignarDocenteAula_deberiaActualizar() {
        var materia = Materia.builder().build();
        materia.setId(1L);
        var grado = Grado.builder().build();
        grado.setId(1L);
        var seccion = new Seccion();
        seccion.setId(1L);
        seccion.setNombre("A");
        var periodo = PeriodoAcademico.builder().nombre("2026").build();
        periodo.setId(1L);
        var curso = Curso.builder().periodo(periodo).grado(grado).seccion(seccion).materia(materia).build();
        curso.setId(1L);

        var docente = new Docente();
        docente.setId(1L);
        docente.setNombres("Juan");
        docente.setApellidos("Perez");

        when(repository.findById(1L)).thenReturn(Optional.of(curso));
        when(docenteRepo.findById(1L)).thenReturn(Optional.of(docente));
        when(repository.save(curso)).thenReturn(curso);

        var res = service.asignarDocenteAula(1L, 1L, null);

        assertEquals(EstadoCurso.ASIGNADO.name(), res.estado());
        assertEquals("Juan Perez", res.docenteNombre());
    }

    @Test
    void listar_deberiaRetornar() {
        when(repository.findAll()).thenReturn(List.of());
        assertTrue(service.listar(null, null, null).isEmpty());
    }
}
