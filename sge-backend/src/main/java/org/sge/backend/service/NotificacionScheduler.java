package org.sge.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sge.backend.model.entity.AlertaFaltas;
import org.sge.backend.model.entity.Notificacion;
import org.sge.backend.repository.AlertaFaltasRepository;
import org.sge.backend.repository.NotificacionRepository;
import org.sge.backend.repository.UsuarioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionScheduler {
    private final AlertaFaltasRepository alertaRepo;
    private final NotificacionRepository notiRepo;
    private final UsuarioRepository usuarioRepo;

    @Transactional
    @Scheduled(fixedRateString = "${app.scheduler.alerta-faltas.rate:3600000}")
    public void procesarAlertasFaltas() {
        List<AlertaFaltas> alertas = alertaRepo.findByEstado("NUEVA");
        if (alertas.isEmpty()) return;
        log.info("Procesando {} alertas de faltas pendientes", alertas.size());

        for (var alerta : alertas) {
            var alumno = alerta.getAlumno();
            var nombreAlumno = alumno.getNombres() + " " + alumno.getApellidos();
            String titulo, cuerpo;

            switch (alerta.getNivel()) {
                case "BAJA" -> {
                    titulo = "Inasistencia registrada";
                    cuerpo = "El alumno " + nombreAlumno + " ha acumulado " + alerta.getCantidadConsecutivas()
                        + " falta(s) consecutiva(s). Se recomienda monitorear su asistencia.";
                }
                case "MEDIA" -> {
                    titulo = "Alerta de faltas - " + nombreAlumno;
                    cuerpo = "El alumno " + nombreAlumno + " presenta " + alerta.getCantidadConsecutivas()
                        + " falta(s) consecutiva(s). Por favor, tomar las medidas necesarias.";
                }
                default -> {
                    titulo = "Alerta CRÍTICA de faltas - " + nombreAlumno;
                    cuerpo = "El alumno " + nombreAlumno + " ha acumulado " + alerta.getCantidadConsecutivas()
                        + " falta(s) consecutiva(s). Se requiere intervención inmediata.";
                }
            }

            for (var padre : alumno.getPadres()) {
                usuarioRepo.findByPersonaId(padre.getId()).ifPresent(usuario -> {
                    var noti = Notificacion.builder()
                        .usuario(usuario).titulo(titulo).cuerpo(cuerpo)
                        .tipo("ALERTA").entidadTipo("AlertaFaltas").entidadId(alerta.getId()).build();
                    notiRepo.save(noti);
                });
            }

            alerta.setEstado("ATENDIDA");
            alertaRepo.save(alerta);
        }

        log.info("Alertas de faltas procesadas exitosamente");
    }
}
