package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.GradoCreateRequest;
import org.sge.backend.model.entity.Grado;
import org.sge.backend.model.enums.NivelEducativo;
import org.sge.backend.repository.GradoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradoServiceTest {

    @Mock private GradoRepository repository;
    private GradoService service;

    @BeforeEach
    void setUp() {
        service = new GradoService(repository);
    }

    @Test
    void listar_sinFiltro_deberiaRetornarTodos() {
        var g = Grado.builder().nombre("1° Primaria").nivel(NivelEducativo.PRIMARIA).orden(1).build();
        when(repository.findAllByOrderByOrden()).thenReturn(List.of(g));

        var res = service.listar(null);

        assertEquals(1, res.size());
    }

    @Test
    void listar_conFiltro_deberiaFiltrarPorNivel() {
        var g = Grado.builder().nombre("1° Primaria").nivel(NivelEducativo.PRIMARIA).orden(1).build();
        when(repository.findByNivelOrderByOrden(NivelEducativo.PRIMARIA)).thenReturn(List.of(g));

        var res = service.listar(NivelEducativo.PRIMARIA);

        assertEquals(1, res.size());
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornar() {
        var g = Grado.builder().nombre("1° Primaria").nivel(NivelEducativo.PRIMARIA).orden(1).build();
        g.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(g));

        var res = service.obtenerPorId(1L);

        assertEquals("1° Primaria", res.nombre());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void crear_deberiaGuardar() {
        var req = new GradoCreateRequest("1° Primaria", NivelEducativo.PRIMARIA, 1, 30);
        when(repository.save(any())).thenAnswer(inv -> {
            var g = inv.<Grado>getArgument(0);
            g.setId(1L);
            return g;
        });

        var res = service.crear(req);

        assertEquals("1° Primaria", res.nombre());
        verify(repository).save(any());
    }
}
