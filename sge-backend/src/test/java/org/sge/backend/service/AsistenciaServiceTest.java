package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.AsistenciaCreateRequest;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.model.entity.AsistenciaAlumno;
import org.sge.backend.model.entity.SesionClase;
import org.sge.backend.model.enums.TipoAsistencia;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.AsistenciaAlumnoRepository;
import org.sge.backend.repository.SesionClaseRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock private AsistenciaAlumnoRepository repository;
    @Mock private SesionClaseRepository sesionRepo;
    @Mock private AlumnoRepository alumnoRepo;
    private AsistenciaService service;

    @BeforeEach
    void setUp() {
        service = new AsistenciaService(repository, sesionRepo, alumnoRepo);
    }

    @Test
    void listarPorSesion_deberiaRetornar() {
        var sesion = new SesionClase();
        sesion.setId(1L);
        var alumno = new Alumno(); alumno.setNombres("Juan"); alumno.setApellidos("Perez");
        var a = AsistenciaAlumno.builder().sesion(sesion).alumno(alumno).tipoAsistencia(TipoAsistencia.PRESENTE).build();
        when(repository.findBySesionId(1L)).thenReturn(List.of(a));

        var res = service.listarPorSesion(1L);

        assertEquals(1, res.size());
    }

    @Test
    void crear_deberiaGuardar() {
        var sesion = new SesionClase(); sesion.setId(1L);
        var alumno = new Alumno(); alumno.setId(1L); alumno.setNombres("Juan"); alumno.setApellidos("Perez");
        when(sesionRepo.findById(1L)).thenReturn(Optional.of(sesion));
        when(alumnoRepo.findById(1L)).thenReturn(Optional.of(alumno));
        when(repository.save(any())).thenAnswer(inv -> {
            var a = inv.<AsistenciaAlumno>getArgument(0);
            a.setId(1L);
            return a;
        });

        var req = new AsistenciaCreateRequest(1L, 1L, TipoAsistencia.PRESENTE, null, null);
        var res = service.crear(req);

        assertNotNull(res.id());
    }

    @Test
    void crearMasivo_deberiaGuardarTodos() {
        var sesion = new SesionClase(); sesion.setId(1L);
        when(sesionRepo.findById(1L)).thenReturn(Optional.of(sesion));
        var alumno = new Alumno(); alumno.setId(1L);
        when(alumnoRepo.findById(1L)).thenReturn(Optional.of(alumno));

        var req = List.of(new AsistenciaCreateRequest(1L, 1L, TipoAsistencia.PRESENTE, null, null));
        var res = service.crearMasivo(req);

        assertTrue(res.alumnoNombre().contains("exitosa"));
        verify(repository, times(1)).save(any());
    }
}
