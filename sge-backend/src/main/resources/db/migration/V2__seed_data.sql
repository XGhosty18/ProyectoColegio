-- Seed data: roles, permisos, estados, tipos evaluacion, transiciones, usuario admin, grados, secciones, periodo, conceptos pago, materias

INSERT INTO permisos (codigo, descripcion, modulo, created_at, updated_at, version)
VALUES ('ACTUAL', 'Ver datos actuales', 'GENERAL', NOW(), NOW(), 0),
       ('HISTORIAL', 'Ver historial', 'GENERAL', NOW(), NOW(), 0),
       ('CONFIG', 'Configurar sistema', 'ADMIN', NOW(), NOW(), 0)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO roles (codigo, nombre, created_at, updated_at, version)
VALUES ('ADMIN', 'Administrador', NOW(), NOW(), 0),
       ('DOCENTE', 'Docente', NOW(), NOW(), 0),
       ('PADRE', 'Padre de Familia', NOW(), NOW(), 0),
       ('ALUMNO', 'Estudiante', NOW(), NOW(), 0)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO roles_permisos (rol_id, permiso_id, created_at, updated_at, version)
SELECT r.id, p.id, NOW(), NOW(), 0
FROM roles r CROSS JOIN permisos p
WHERE r.codigo = 'ADMIN' AND p.codigo IN ('ACTUAL', 'HISTORIAL', 'CONFIG')
ON CONFLICT DO NOTHING;

INSERT INTO roles_permisos (rol_id, permiso_id, created_at, updated_at, version)
SELECT r.id, p.id, NOW(), NOW(), 0
FROM roles r, permisos p
WHERE r.codigo = 'DOCENTE' AND p.codigo = 'ACTUAL'
ON CONFLICT DO NOTHING;

INSERT INTO roles_permisos (rol_id, permiso_id, created_at, updated_at, version)
SELECT r.id, p.id, NOW(), NOW(), 0
FROM roles r, permisos p
WHERE r.codigo = 'PADRE' AND p.codigo IN ('ACTUAL', 'HISTORIAL')
ON CONFLICT DO NOTHING;

INSERT INTO roles_permisos (rol_id, permiso_id, created_at, updated_at, version)
SELECT r.id, p.id, NOW(), NOW(), 0
FROM roles r, permisos p
WHERE r.codigo = 'ALUMNO' AND p.codigo = 'ACTUAL'
ON CONFLICT DO NOTHING;

INSERT INTO estados_alumno (codigo, nombre, es_terminal, es_transitorio, permiso_acceso, created_at, updated_at, version)
VALUES ('ACTIVO', 'Activo', false, false, true, NOW(), NOW(), 0),
       ('RETIRADO', 'Retirado', true, false, false, NOW(), NOW(), 0),
       ('EXPULSADO', 'Expulsado', true, false, false, NOW(), NOW(), 0),
       ('SUSPENDIDO', 'Suspendido', false, true, true, NOW(), NOW(), 0),
       ('TRASLADADO', 'Trasladado', true, false, false, NOW(), NOW(), 0),
       ('EGRESADO', 'Egresado', true, false, true, NOW(), NOW(), 0)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO transiciones_estado (estado_origen_id, estado_destino_id, codigo_gatillante, es_automatica, requiere_admin, requiere_consejo, notifica_padre, created_at, updated_at, version)
SELECT o.id, d.id, gat.codigo, gat.auto, gat.admin, gat.consejo, gat.notifica, NOW(), NOW(), 0
FROM (VALUES ('ACTIVO', 'SUSPENDIDO', 'FALTAS', true, false, false, true),
             ('ACTIVO', 'RETIRADO', 'RETIRO_VOLUNTARIO', false, true, false, true),
             ('SUSPENDIDO', 'ACTIVO', 'REINCORPORACION', false, true, false, true),
             ('ACTIVO', 'EXPULSADO', 'FALTA_GRAVE', false, true, true, true),
             ('ACTIVO', 'TRASLADADO', 'TRASLADO', false, true, false, true),
             ('ACTIVO', 'EGRESADO', 'EGRESO', true, false, false, false)) AS gat(origen, destino, codigo, auto, admin, consejo, notifica)
JOIN estados_alumno o ON o.codigo = gat.origen
JOIN estados_alumno d ON d.codigo = gat.destino
ON CONFLICT (estado_origen_id, estado_destino_id, codigo_gatillante) DO NOTHING;

INSERT INTO tipos_evaluacion (nombre, peso_porcentaje, orden, created_at, updated_at, version)
VALUES ('Práctica Calificada', 20.0, 1, NOW(), NOW(), 0),
       ('Examen Parcial', 30.0, 2, NOW(), NOW(), 0),
       ('Examen Final', 40.0, 3, NOW(), NOW(), 0),
       ('Trabajo/Proyecto', 10.0, 4, NOW(), NOW(), 0);

INSERT INTO grados (nombre, nivel, orden, capacidad_max, created_at, updated_at, version)
VALUES ('Primaria', 'PRIMARIA', 2, 40, NOW(), NOW(), 0),
       ('Secundaria', 'SECUNDARIA', 3, 40, NOW(), NOW(), 0);

INSERT INTO secciones (nombre, capacidad, grado_id, created_at, updated_at, version)
SELECT 'A', 30, g.id, NOW(), NOW(), 0 FROM grados g WHERE g.nombre IN ('Primaria', 'Secundaria')
UNION ALL
SELECT 'B', 30, g.id, NOW(), NOW(), 0 FROM grados g WHERE g.nombre IN ('Primaria', 'Secundaria');

INSERT INTO periodos_academicos (nombre, codigo, fecha_inicio, fecha_fin, estado, created_at, updated_at, version)
VALUES ('Año Escolar 2026', '2026', '2026-03-01', '2026-12-20', 'PLANIFICACION', NOW(), NOW(), 0);

INSERT INTO conceptos_pago (nombre, monto_base, periodicidad, created_at, updated_at, version)
VALUES ('Matrícula', 250.00, 'ANUAL', NOW(), NOW(), 0),
       ('Pensión Mensual', 180.00, 'MENSUAL', NOW(), NOW(), 0);

INSERT INTO materias (nombre, codigo, horas_semanales_req, tipo, created_at, updated_at, version)
VALUES ('Matemática', 'MAT01', 6, 'TRONCO', NOW(), NOW(), 0),
       ('Comunicación', 'COM01', 6, 'TRONCO', NOW(), NOW(), 0),
       ('Ciencia y Tecnología', 'CT01', 4, 'TRONCO', NOW(), NOW(), 0),
       ('Inglés', 'ING01', 4, 'TRONCO', NOW(), NOW(), 0),
       ('Arte', 'ART01', 2, 'TALLER', NOW(), NOW(), 0);


