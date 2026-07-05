package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.DocenteCreateRequest;
import org.sge.backend.model.entity.Docente;
import org.sge.backend.repository.DocenteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocenteServiceTest {

    @Mock private DocenteRepository repository;
    private DocenteService service;

    @BeforeEach
    void setUp() {
        service = new DocenteService(repository);
    }

    @Test
    void listar_deberiaRetornarTodos() {
        var d = new Docente();
        d.setNombres("Juan");
        d.setApellidos("Perez");
        when(repository.findAll()).thenReturn(List.of(d));

        var res = service.listar();

        assertEquals(1, res.size());
    }

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornar() {
        var d = new Docente();
        d.setId(1L);
        d.setNombres("Maria");
        d.setApellidos("Lopez");
        when(repository.findById(1L)).thenReturn(Optional.of(d));

        var res = service.obtenerPorId(1L);

        assertEquals("Maria", res.nombres());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void crear_deberiaGuardarConCargaHorariaDefault() {
        var req = new DocenteCreateRequest("Juan", "Perez", "12345678", LocalDate.of(1990, 1, 1),
            "M", "999999999", "Av. Siempre Viva", "EMP001", "Matemáticas", "TIEMPO_COMPLETO", null);
        when(repository.save(any())).thenAnswer(inv -> {
            var d = inv.<Docente>getArgument(0);
            d.setId(1L);
            return d;
        });

        var res = service.crear(req);

        assertEquals(40, res.cargaHorariaMax());
        assertEquals("EMP001", res.codigoEmpleado());
    }
}
