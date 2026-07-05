package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.model.entity.Notificacion;
import org.sge.backend.model.entity.Usuario;
import org.sge.backend.repository.NotificacionRepository;
import org.sge.backend.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock private NotificacionRepository repository;
    @Mock private UsuarioRepository usuarioRepo;
    private NotificacionService service;

    @BeforeEach
    void setUp() {
        service = new NotificacionService(repository, usuarioRepo);
    }

    @Test
    void listarPorUsuario_deberiaRetornarNotificaciones() {
        var usuario = new Usuario();
        usuario.setId(1L);
        var notif = Notificacion.builder().usuario(usuario).titulo("Test").cuerpo("Cuerpo").build();
        when(repository.findByUsuarioIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(notif));

        var res = service.listarPorUsuario(1L);

        assertEquals(1, res.size());
        assertEquals("Test", res.getFirst().titulo());
    }

    @Test
    void pendientes_deberiaRetornarNoLeidas() {
        var usuario = new Usuario();
        usuario.setId(1L);
        var notif = Notificacion.builder().usuario(usuario).leida(false).build();
        when(repository.findByLeidaFalseAndUsuarioId(1L)).thenReturn(List.of(notif));

        var res = service.pendientes(1L);

        assertEquals(1, res.size());
        assertFalse(res.getFirst().leida());
    }

    @Test
    void marcarLeida_deberiaActualizar() {
        var notif = Notificacion.builder().leida(false).build();
        notif.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(notif));

        service.marcarLeida(1L);

        assertTrue(notif.getLeida());
        verify(repository).save(notif);
    }
}
