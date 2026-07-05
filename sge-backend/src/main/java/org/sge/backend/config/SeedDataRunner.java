package org.sge.backend.config;

import lombok.RequiredArgsConstructor;
import org.sge.backend.model.entity.*;
import org.sge.backend.model.enums.*;
import org.sge.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {

    private final RolRepository rolRepo;
    private final PermisoRepository permisoRepo;
    private final RolPermisoRepository rpRepo;
    private final EstadoAlumnoRepository estadoRepo;
    private final TipoEvaluacionRepository tipoEvalRepo;
    private final TransicionEstadoRepository transicionRepo;
    private final UsuarioRepository usuarioRepo;
    private final UsuarioRolRepository urRepo;
    private final GradoRepository gradoRepo;
    private final SeccionRepository seccionRepo;
    private final PeriodoAcademicoRepository periodoRepo;
    private final ConceptoPagoRepository conceptoPagoRepo;
    private final MateriaRepository materiaRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepo.findByUsername("admin").isEmpty()) {
            var rolAdmin = rolRepo.findByCodigo("ADMIN").orElse(null);
            if (rolAdmin != null) {
                var admin = new Usuario();
                admin.setUsername("admin");
                admin.setEmail("admin@sge.edu");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setEnabled(true);
                admin = usuarioRepo.save(admin);
                urRepo.save(UsuarioRol.builder().usuario(admin).rol(rolAdmin).build());
            }
        }

        if (rolRepo.count() > 0) return;

        var permisoActual = permisoRepo.save(Permiso.builder().codigo("ACTUAL").descripcion("Ver datos actuales").build());
        var permisoHistorial = permisoRepo.save(Permiso.builder().codigo("HISTORIAL").descripcion("Ver historial").build());
        var permisoConfig = permisoRepo.save(Permiso.builder().codigo("CONFIG").descripcion("Configurar sistema").build());

        var rolAdmin = rolRepo.save(Rol.builder().codigo("ADMIN").nombre("Administrador").build());
        var rolDocente = rolRepo.save(Rol.builder().codigo("DOCENTE").nombre("Docente").build());
        var rolPadre = rolRepo.save(Rol.builder().codigo("PADRE").nombre("Padre de Familia").build());
        var rolAlumno = rolRepo.save(Rol.builder().codigo("ALUMNO").nombre("Estudiante").build());

        rpRepo.save(RolPermiso.builder().rol(rolAdmin).permiso(permisoActual).build());
        rpRepo.save(RolPermiso.builder().rol(rolAdmin).permiso(permisoHistorial).build());
        rpRepo.save(RolPermiso.builder().rol(rolAdmin).permiso(permisoConfig).build());
        rpRepo.save(RolPermiso.builder().rol(rolDocente).permiso(permisoActual).build());
        rpRepo.save(RolPermiso.builder().rol(rolPadre).permiso(permisoActual).build());
        rpRepo.save(RolPermiso.builder().rol(rolPadre).permiso(permisoHistorial).build());
        rpRepo.save(RolPermiso.builder().rol(rolAlumno).permiso(permisoActual).build());

        var activo = estadoRepo.save(EstadoAlumno.builder().codigo("ACTIVO").nombre("Activo").esTerminal(false).esTransitorio(false).permisoAcceso(true).build());
        var retirado = estadoRepo.save(EstadoAlumno.builder().codigo("RETIRADO").nombre("Retirado").esTerminal(true).esTransitorio(false).permisoAcceso(false).build());
        var expulsado = estadoRepo.save(EstadoAlumno.builder().codigo("EXPULSADO").nombre("Expulsado").esTerminal(true).esTransitorio(false).permisoAcceso(false).build());
        var suspendido = estadoRepo.save(EstadoAlumno.builder().codigo("SUSPENDIDO").nombre("Suspendido").esTerminal(false).esTransitorio(true).permisoAcceso(true).build());
        var trasladado = estadoRepo.save(EstadoAlumno.builder().codigo("TRASLADADO").nombre("Trasladado").esTerminal(true).esTransitorio(false).permisoAcceso(false).build());
        var egresado = estadoRepo.save(EstadoAlumno.builder().codigo("EGRESADO").nombre("Egresado").esTerminal(true).esTransitorio(false).permisoAcceso(true).build());

        transicionRepo.saveAll(List.of(
            TransicionEstado.builder().estadoOrigen(activo).estadoDestino(suspendido).codigoGatillante("FALTAS").esAutomatica(true).requiereAdmin(false).requiereConsejo(false).notificaPadre(true).build(),
            TransicionEstado.builder().estadoOrigen(activo).estadoDestino(retirado).codigoGatillante("RETIRO_VOLUNTARIO").esAutomatica(false).requiereAdmin(true).requiereConsejo(false).notificaPadre(true).build(),
            TransicionEstado.builder().estadoOrigen(suspendido).estadoDestino(activo).codigoGatillante("REINCORPORACION").esAutomatica(false).requiereAdmin(true).requiereConsejo(false).notificaPadre(true).build(),
            TransicionEstado.builder().estadoOrigen(activo).estadoDestino(expulsado).codigoGatillante("FALTA_GRAVE").esAutomatica(false).requiereAdmin(true).requiereConsejo(true).notificaPadre(true).build(),
            TransicionEstado.builder().estadoOrigen(activo).estadoDestino(trasladado).codigoGatillante("TRASLADO").esAutomatica(false).requiereAdmin(true).requiereConsejo(false).notificaPadre(true).build(),
            TransicionEstado.builder().estadoOrigen(activo).estadoDestino(egresado).codigoGatillante("EGRESO").esAutomatica(true).requiereAdmin(false).requiereConsejo(false).notificaPadre(false).build()
        ));

        tipoEvalRepo.saveAll(List.of(
            TipoEvaluacion.builder().nombre("Práctica Calificada").pesoPorcentaje(20.0).orden(1).build(),
            TipoEvaluacion.builder().nombre("Examen Parcial").pesoPorcentaje(30.0).orden(2).build(),
            TipoEvaluacion.builder().nombre("Examen Final").pesoPorcentaje(40.0).orden(3).build(),
            TipoEvaluacion.builder().nombre("Trabajo/Proyecto").pesoPorcentaje(10.0).orden(4).build()
        ));

        var primaria = gradoRepo.save(Grado.builder().nombre("Primaria").nivel(NivelEducativo.PRIMARIA).orden(2).capacidadMax(40).build());
        var secundaria = gradoRepo.save(Grado.builder().nombre("Secundaria").nivel(NivelEducativo.SECUNDARIA).orden(3).capacidadMax(40).build());

        seccionRepo.saveAll(List.of(
            Seccion.builder().nombre("A").capacidad(30).grado(primaria).build(),
            Seccion.builder().nombre("B").capacidad(30).grado(primaria).build(),
            Seccion.builder().nombre("A").capacidad(30).grado(secundaria).build(),
            Seccion.builder().nombre("B").capacidad(30).grado(secundaria).build()
        ));

        periodoRepo.save(PeriodoAcademico.builder()
            .nombre("Año Escolar 2026").codigo("2026")
            .fechaInicio(LocalDate.of(2026, 3, 1))
            .fechaFin(LocalDate.of(2026, 12, 20))
            .build());

        conceptoPagoRepo.saveAll(List.of(
            ConceptoPago.builder().nombre("Matrícula").montoBase(java.math.BigDecimal.valueOf(250)).periodicidad("ANUAL").build(),
            ConceptoPago.builder().nombre("Pensión Mensual").montoBase(java.math.BigDecimal.valueOf(180)).periodicidad("MENSUAL").build()
        ));

        materiaRepo.saveAll(List.of(
            Materia.builder().nombre("Matemática").codigo("MAT01").horasSemanalesReq(6).tipo(TipoMateria.TRONCO).build(),
            Materia.builder().nombre("Comunicación").codigo("COM01").horasSemanalesReq(6).tipo(TipoMateria.TRONCO).build(),
            Materia.builder().nombre("Ciencia y Tecnología").codigo("CT01").horasSemanalesReq(4).tipo(TipoMateria.TRONCO).build(),
            Materia.builder().nombre("Inglés").codigo("ING01").horasSemanalesReq(4).tipo(TipoMateria.TRONCO).build(),
            Materia.builder().nombre("Arte").codigo("ART01").horasSemanalesReq(2).tipo(TipoMateria.TALLER).build()
        ));
    }
}
