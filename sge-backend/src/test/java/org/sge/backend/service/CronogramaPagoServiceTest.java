package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.model.entity.CronogramaPago;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.model.entity.ConceptoPago;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.ConceptoPagoRepository;
import org.sge.backend.repository.CronogramaPagoRepository;
import org.sge.backend.repository.PeriodoAcademicoRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CronogramaPagoServiceTest {

    @Mock private CronogramaPagoRepository repository;
    @Mock private AlumnoRepository alumnoRepo;
    @Mock private ConceptoPagoRepository conceptoRepo;
    @Mock private PeriodoAcademicoRepository periodoRepo;
    private CronogramaPagoService service;

    @BeforeEach
    void setUp() {
        service = new CronogramaPagoService(repository, alumnoRepo, conceptoRepo, periodoRepo);
    }

    @Test
    void listarPorAlumno_deberiaRetornarCronogramas() {
        var alumno = new Alumno();
        alumno.setNombres("Juan");
        var cp = ConceptoPago.builder().nombre("Pensión").build();
        var crono = CronogramaPago.builder().alumno(alumno).conceptoPago(cp).monto(BigDecimal.valueOf(300)).build();
        when(repository.findByAlumnoIdOrderByFechaVencimiento(1L)).thenReturn(List.of(crono));

        var res = service.listarPorAlumno(1L);

        assertEquals(1, res.size());
        assertEquals("Pensión", res.getFirst().conceptoNombre());
    }
}
