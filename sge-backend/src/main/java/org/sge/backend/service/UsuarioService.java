package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import org.sge.backend.dto.request.UsuarioCreateRequest;
import org.sge.backend.dto.response.UsuarioResponse;
import org.sge.backend.model.entity.Usuario;
import org.sge.backend.model.entity.UsuarioRol;
import org.sge.backend.repository.PersonaRepository;
import org.sge.backend.repository.RolRepository;
import org.sge.backend.repository.UsuarioRepository;
import org.sge.backend.repository.UsuarioRolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final RolRepository rolRepo;
    private final PersonaRepository personaRepo;
    private final UsuarioRolRepository urRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioCreateRequest req) {
        var usuario = new Usuario();
        usuario.setUsername(req.username());
        usuario.setEmail(req.email());
        usuario.setPasswordHash(passwordEncoder.encode(req.password()));
        if (req.personaId() != null) {
            var persona = personaRepo.findById(req.personaId()).orElseThrow();
            usuario.setPersona(persona);
        }
        usuario = repository.save(usuario);
        if (req.rolIds() != null) {
            for (var rid : req.rolIds()) {
                var rol = rolRepo.findById(rid).orElseThrow();
                urRepo.save(UsuarioRol.builder().usuario(usuario).rol(rol).build());
            }
        }
        return toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioCreateRequest req) {
        var u = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setUsername(req.username());
        u.setEmail(req.email());
        if (req.password() != null && !req.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.password()));
        }
        if (req.personaId() != null) {
            var persona = personaRepo.findById(req.personaId()).orElseThrow();
            u.setPersona(persona);
        } else {
            u.setPersona(null);
        }
        u = repository.save(u);
        urRepo.findByUsuarioId(id).forEach(ur -> urRepo.delete(ur));
        if (req.rolIds() != null) {
            for (var rid : req.rolIds()) {
                var rol = rolRepo.findById(rid).orElseThrow();
                urRepo.save(UsuarioRol.builder().usuario(u).rol(rol).build());
            }
        }
        return toResponse(u);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Usuario no encontrado");
        repository.deleteById(id);
    }

    private UsuarioResponse toResponse(Usuario u) {
        var roles = urRepo.findByUsuarioId(u.getId()).stream().map(ur -> ur.getRol().getCodigo()).toList();
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getEmail(), u.getEnabled(),
            u.getPersona() != null ? u.getPersona().getId() : null,
            u.getPersona() != null ? u.getPersona().getNombres() + " " + u.getPersona().getApellidos() : null,
            roles);
    }
}
