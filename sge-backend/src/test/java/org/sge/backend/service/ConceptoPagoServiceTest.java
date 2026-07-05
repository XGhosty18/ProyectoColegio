package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.model.entity.ConceptoPago;
import org.sge.backend.repository.ConceptoPagoRepository;
import org.sge.backend.repository.GradoRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConceptoPagoServiceTest {

    @Mock private ConceptoPagoRepository repository;
    @Mock private GradoRepository gradoRepo;
    private ConceptoPagoService service;

    @BeforeEach
    void setUp() {
        service = new ConceptoPagoService(repository, gradoRepo);
    }

    @Test
    void listar_deberiaRetornarTodos() {
        var cp = ConceptoPago.builder().nombre("Matrícula").montoBase(BigDecimal.valueOf(500)).periodicidad("ANUAL").build();
        when(repository.findAll()).thenReturn(List.of(cp));

        var res = service.listar();

        assertEquals(1, res.size());
        assertEquals("Matrícula", res.getFirst().nombre());
    }
}
