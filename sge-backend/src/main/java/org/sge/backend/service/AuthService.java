package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.LoginRequest;
import org.sge.backend.dto.request.RegisterRequest;
import org.sge.backend.dto.response.TokenResponse;
import org.sge.backend.exception.BusinessRuleViolationException;
import org.sge.backend.model.entity.Usuario;
import org.sge.backend.model.entity.UsuarioRol;
import org.sge.backend.repository.RolRepository;
import org.sge.backend.repository.UsuarioRepository;
import org.sge.backend.repository.UsuarioRolRepository;
import org.sge.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioRolRepository usuarioRolRepo;
    private final RolRepository rolRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> resetCodes = new ConcurrentHashMap<>();

    public TokenResponse refresh(String refreshToken) {
        if (!jwtService.isValid(refreshToken))
            throw new BusinessRuleViolationException("TOKEN_INVALIDO", "Token inválido o expirado");
        var username = jwtService.extractUsername(refreshToken);
        var usuario = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new BusinessRuleViolationException("USUARIO_NO_ENCONTRADO", "Usuario no encontrado"));
        return buildTokenResponse(usuario);
    }

    @Transactional
    public TokenResponse login(LoginRequest req) {
        var usuario = usuarioRepo.findByUsername(req.username())
            .orElseThrow(() -> new BusinessRuleViolationException("CREDENCIALES_INVALIDAS", "Usuario o contraseña incorrectos"));
        if (!passwordEncoder.matches(req.password(), usuario.getPasswordHash()))
            throw new BusinessRuleViolationException("CREDENCIALES_INVALIDAS", "Usuario o contraseña incorrectos");
        return buildTokenResponse(usuario);
    }

    @Transactional
    public TokenResponse register(RegisterRequest req) {
        if (usuarioRepo.existsByUsername(req.username()))
            throw new BusinessRuleViolationException("USERNAME_EXISTE", "El nombre de usuario ya está en uso");
        if (usuarioRepo.existsByEmail(req.email()))
            throw new BusinessRuleViolationException("EMAIL_EXISTE", "El email ya está registrado");

        var usuario = new Usuario();
        usuario.setUsername(req.username());
        usuario.setEmail(req.email());
        usuario.setPasswordHash(passwordEncoder.encode(req.password()));
        usuario.setEnabled(true);
        usuario = usuarioRepo.save(usuario);

        if (req.rolCodigo() != null) {
            var rol = rolRepo.findByCodigo(req.rolCodigo()).orElseThrow();
            var ur = UsuarioRol.builder().usuario(usuario).rol(rol).build();
            usuarioRolRepo.save(ur);
        }
        return buildTokenResponse(usuario);
    }

    public void forgotPassword(String email) {
        usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new BusinessRuleViolationException("EMAIL_NO_ENCONTRADO", "No existe una cuenta con ese email"));
        var code = Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[6]);
        resetCodes.put(email, code);
        System.out.println("=== RESET CODE for " + email + ": " + code + " ===");
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        var stored = resetCodes.get(email);
        if (stored == null || !stored.equals(code))
            throw new BusinessRuleViolationException("CODIGO_INVALIDO", "Código de recuperación inválido o expirado");
        var usuario = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new BusinessRuleViolationException("EMAIL_NO_ENCONTRADO", "No existe una cuenta con ese email"));
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuarioRepo.save(usuario);
        resetCodes.remove(email);
    }

    private TokenResponse buildTokenResponse(Usuario usuario) {
        var usuarioRoles = usuarioRolRepo.findByUsuarioId(usuario.getId());
        List<String> roleCodes = usuarioRoles.stream()
            .map(ur -> ur.getRol().getCodigo())
            .toList();

        List<String> permissions = usuarioRoles.stream()
            .flatMap(ur -> ur.getRol().getRolPermisos().stream())
            .map(rp -> rp.getPermiso().getCodigo())
            .distinct()
            .toList();

        if (roleCodes.isEmpty()) {
            roleCodes = List.of("USER");
        }

        var token = jwtService.generateToken(usuario.getUsername(), roleCodes, permissions);
        var refreshToken = jwtService.generateToken(usuario.getUsername(), roleCodes, permissions);
        return new TokenResponse(token, refreshToken, usuario.getUsername(), usuario.getEmail(), roleCodes);
    }
}
