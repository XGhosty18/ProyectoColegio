package org.sge.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sge.backend.dto.request.LoginRequest;
import org.sge.backend.dto.request.RegisterRequest;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Rol;
import org.sge.backend.model.entity.Usuario;
import org.sge.backend.model.entity.UsuarioRol;
import org.sge.backend.repository.RolRepository;
import org.sge.backend.repository.UsuarioRepository;
import org.sge.backend.repository.UsuarioRolRepository;
import org.sge.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepo;
    @Mock private UsuarioRolRepository usuarioRolRepo;
    @Mock private RolRepository rolRepo;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(usuarioRepo, usuarioRolRepo, rolRepo, jwtService, passwordEncoder);
    }

    @Test
    void login_conCredencialesValidas_deberiaRetornarToken() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setPasswordHash("hash");
        when(usuarioRepo.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("pass123", "hash")).thenReturn(true);
        when(usuarioRolRepo.findByUsuarioId(1L)).thenReturn(List.of());
        when(jwtService.generateToken("admin", "USER")).thenReturn("token.jwt");

        var res = service.login(new LoginRequest("admin", "pass123"));

        assertEquals("token.jwt", res.token());
        assertEquals("USER", res.role());
    }

    @Test
    void login_conPasswordIncorrecto_deberiaLanzarExcepcion() {
        var usuario = new Usuario();
        usuario.setPasswordHash("hash");
        when(usuarioRepo.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(BusinessRuleViolationException.class,
            () -> service.login(new LoginRequest("admin", "wrong")));
    }

    @Test
    void register_deberiaCrearUsuario() {
        when(usuarioRepo.existsByUsername("nuevo")).thenReturn(false);
        when(usuarioRepo.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(usuarioRepo.save(any())).thenAnswer(inv -> {
            var u = inv.<Usuario>getArgument(0);
            u.setId(1L);
            return u;
        });
        var rol = Rol.builder().codigo("ADMIN").build();
        when(rolRepo.findByCodigo("ADMIN")).thenReturn(Optional.of(rol));
        when(jwtService.generateToken("nuevo", "ADMIN")).thenReturn("token.jwt");

        var res = service.register(new RegisterRequest("nuevo", "nuevo@test.com", "pass123", null, "ADMIN"));

        assertEquals("token.jwt", res.token());
        verify(usuarioRolRepo).save(any());
    }

    @Test
    void register_usernameDuplicado_deberiaLanzarExcepcion() {
        when(usuarioRepo.existsByUsername("existente")).thenReturn(true);
        assertThrows(BusinessRuleViolationException.class,
            () -> service.register(new RegisterRequest("existente", "e@test.com", "pass", null, null)));
    }

    @Test
    void refresh_conTokenValido_deberiaRetornarNuevoToken() {
        when(jwtService.isValid("old.token")).thenReturn(true);
        when(jwtService.extractUsername("old.token")).thenReturn("admin");
        when(jwtService.extractRole("old.token")).thenReturn("ADMIN");
        when(jwtService.generateToken("admin", "ADMIN")).thenReturn("new.token");

        var res = service.refresh("old.token");

        assertEquals("new.token", res.token());
    }

    @Test
    void refresh_conTokenInvalido_deberiaLanzarExcepcion() {
        when(jwtService.isValid("bad.token")).thenReturn(false);
        assertThrows(BusinessRuleViolationException.class, () -> service.refresh("bad.token"));
    }
}
