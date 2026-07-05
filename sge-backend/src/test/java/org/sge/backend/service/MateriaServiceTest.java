package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.MateriaCreateRequest;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Materia;
import org.sge.backend.model.enums.TipoMateria;
import org.sge.backend.repository.MateriaRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

    @Mock private MateriaRepository repository;
    private MateriaService service;

    @BeforeEach
    void setUp() {
        service = new MateriaService(repository);
    }

    @Test
    void listar_deberiaRetornarTodas() {
        var m = Materia.builder().nombre("Matemáticas").codigo("MAT01").horasSemanalesReq(6).tipo(TipoMateria.TRONCO).build();
        when(repository.findAll()).thenReturn(List.of(m));

        var res = service.listar();

        assertEquals(1, res.size());
    }

    @Test
    void crear_deberiaGuardar() {
        var req = new MateriaCreateRequest("Matemáticas", "MAT01", 6, TipoMateria.TRONCO);
        when(repository.findByCodigo("MAT01")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> {
            var m = inv.<Materia>getArgument(0);
            m.setId(1L);
            return m;
        });

        var res = service.crear(req);

        assertEquals("MAT01", res.codigo());
    }

    @Test
    void crear_codigoDuplicado_deberiaLanzarExcepcion() {
        var req = new MateriaCreateRequest("Matemáticas", "MAT01", 6, TipoMateria.TRONCO);
        when(repository.findByCodigo("MAT01")).thenReturn(Optional.of(Materia.builder().build()));

        assertThrows(BusinessRuleViolationException.class, () -> service.crear(req));
    }
}
