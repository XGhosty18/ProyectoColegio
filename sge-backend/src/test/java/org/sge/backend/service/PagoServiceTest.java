package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.model.entity.Pago;
import org.sge.backend.model.entity.Alumno;
import org.sge.backend.model.entity.CronogramaPago;
import org.sge.backend.repository.AlumnoRepository;
import org.sge.backend.repository.CronogramaPagoRepository;
import org.sge.backend.repository.PagoRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock private PagoRepository repository;
    @Mock private AlumnoRepository alumnoRepo;
    @Mock private CronogramaPagoRepository cronogramaRepo;
    private PagoService service;

    @BeforeEach
    void setUp() {
        service = new PagoService(repository, alumnoRepo, cronogramaRepo);
    }

    @Test
    void listarPorAlumno_deberiaRetornarPagos() {
        var alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombres("Juan");
        alumno.setApellidos("Perez");
        var cronograma = CronogramaPago.builder().build();
        cronograma.setId(1L);
        var pago = Pago.builder().alumno(alumno).cronogramaPago(cronograma).monto(BigDecimal.valueOf(500)).metodo("EFECTIVO").build();
        when(repository.findByAlumnoIdOrderByFechaPagoDesc(1L)).thenReturn(List.of(pago));

        var res = service.listarPorAlumno(1L);

        assertEquals(1, res.size());
        assertEquals("EFECTIVO", res.getFirst().metodo());
    }
}
