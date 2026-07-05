package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.model.entity.TipoEvaluacion;
import org.sge.backend.repository.TipoEvaluacionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoEvaluacionServiceTest {

    @Mock private TipoEvaluacionRepository repository;
    private TipoEvaluacionService service;

    @BeforeEach
    void setUp() {
        service = new TipoEvaluacionService(repository);
    }

    @Test
    void listar_deberiaRetornarTodos() {
        var te = TipoEvaluacion.builder().nombre("Examen").pesoPorcentaje(40.0).orden(1).build();
        when(repository.findAll()).thenReturn(List.of(te));

        var res = service.listar();

        assertEquals(1, res.size());
        assertEquals("Examen", res.getFirst().nombre());
    }
}
