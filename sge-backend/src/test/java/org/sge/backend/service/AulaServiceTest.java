package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.AulaCreateRequest;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Aula;
import org.sge.backend.model.enums.TipoAula;
import org.sge.backend.repository.AulaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AulaServiceTest {

    @Mock private AulaRepository repository;
    private AulaService service;

    @BeforeEach
    void setUp() {
        service = new AulaService(repository);
    }

    @Test
    void listar_deberiaRetornarTodas() {
        var aula = Aula.builder().nombre("Aula 101").codigo("A101").capacidad(30).tipo(TipoAula.COMUN).build();
        when(repository.findAll()).thenReturn(List.of(aula));

        var res = service.listar();

        assertEquals(1, res.size());
    }

    @Test
    void crear_deberiaGuardar() {
        var req = new AulaCreateRequest("Aula 101", "A101", 30, TipoAula.COMUN);
        when(repository.findByCodigo("A101")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            var a = inv.<Aula>getArgument(0);
            a.setId(1L);
            return a;
        });

        var res = service.crear(req);

        assertEquals("A101", res.codigo());
        verify(repository).save(any());
    }

    @Test
    void crear_codigoDuplicado_deberiaLanzarExcepcion() {
        var req = new AulaCreateRequest("Aula 101", "A101", 30, TipoAula.COMUN);
        when(repository.findByCodigo("A101")).thenReturn(Optional.of(Aula.builder().build()));

        assertThrows(BusinessRuleViolationException.class, () -> service.crear(req));
        verify(repository, never()).save(any());
    }
}
