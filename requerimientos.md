# SISTEMA DE GESTIÓN ESCOLAR (SGE) - ESPECIFICACIÓN DE REQUERIMIENTOS

**Versión:** 1.0  
**Fecha:** 2026-07-04  
**Estado:** Baseline para desarrollo  
**Stack:** PostgreSQL 16+, Java 21 + Spring Boot 3.3+, Angular 18+ (Standalone, Signals, Zoneless), SCSS, Figma  

---

## 1. ARQUITECTURA DE ROLES Y PERMISOS

### 1.1 Jerarquía de Roles (Niveles de Privilegio)

```
NIVEL 4: ADMINISTRADOR (Control total del sistema)
    │
    ├── NIVEL 3: DOCENTE (Gestión académica de sus cursos)
    │
    ├── NIVEL 2: ALUMNO (Acceso a su información académica)
    │
    └── NIVEL 2: PADRE DE FAMILIA (Seguimiento de sus hijos)
```

**Principios Rectores:**
1. **Mínimo Privilegio:** Cada rol solo ve y ejecuta lo estrictamente necesario.
2. **Separación de Responsabilidades:** Ningún rol concentra funciones que permitan conflictos de interés (ej. Docente no puede cambiar estado de alumno; Admin no puede modificar notas sin auditoría dual).
3. **Trazabilidad Total:** Toda acción sobre datos críticos (notas, estados, pagos) queda registrada con *quién, cuándo, por qué*.
4. **Estados Gobernados por Transiciones:** Los cambios de estado de un alumno solo ocurren mediante transiciones explícitas validadas por reglas de negocio.

### 1.2 Matriz de Permisos por Rol

#### ADMINISTRADOR
| Dimensión | Permisos (Códigos) | Tipo | Detalle Crítico |
|-----------|-------------------|------|-----------------|
| **Usuarios** | `USUARIO_CREAR`, `USUARIO_LEER`, `USUARIO_ACTUALIZAR`, `USUARIO_ELIMINAR`, `USUARIO_ASIGNAR_ROL` | CRUD | No puede eliminarse a sí mismo. |
| **Académico** | `CURSO_CRUD`, `GRADO_CRUD`, `PERIODO_CRUD`, `HORARIO_CRUD`, `MATERIA_CRUD` | CRUD | Plan de estudios, horarios globales, asignación docente-aula. |
| **Notas** | `NOTA_LECTURA`, `NOTA_AUDITORIA` | Lectura / Especial | Modificar notas **SOLO** con motivo + aprobación de segundo ADMIN (dual control). |
| **Alumnos** | `ALUMNO_CAMBIAR_ESTADO`, `ALUMNO_MATRICULAR`, `ALUMNO_REINGRESAR` | CRUD | Ejecuta cualquier transición de la máquina de estados. |
| **Pagos** | `PAGO_LECTURA`, `PAGO_REPORTE`, `PAGO_AJUSTE` | Lectura / Reporte / CRUD | Ajustes/descuentos requieren motivo obligatorio. |
| **Disciplina** | `SANCION_CREAR`, `SANCION_REVERTIR`, `EXPULSION_AUTORIZAR` | CRUD / Especial | Expulsión requiere Consejo Directivo. |
| **Configuración** | `CONFIG_SISTEMA`, `CONFIG_NOTIFICACIONES` | CRUD | Parámetros, montos, plantillas de notificación. |
| **Reportes** | `REPORTE_ACADEMICO`, `REPORTE_FINANCIERO`, `REPORTE_AUDITORIA` | Reporte | Exportación masiva PDF/Excel con gráficos. |
| **Seguridad** | `AUDITORIA_LEER`, `BACKUP_GESTION` | Lectura / Especial | Logs inmutables, respaldos programados. |

#### DOCENTE
| Dimensión | Permisos | Tipo | Restricciones |
|-----------|----------|------|---------------|
| **Perfil** | `PERFIL_LEER`, `PERFIL_EDITAR` | Lectura/Actualización | No puede editar salario ni datos contractuales. |
| **Cursos** | `CURSO_ASIGNADO_LEER`, `CURSO_ASIGNADO_HORARIO` | Lectura | Solo cursos asignados en período activo. |
| **Alumnos** | `ALUMNO_CURSO_LEER` | Lectura | Datos básicos + contacto emergencia de *sus* alumnos. |
| **Notas** | `NOTA_CREAR`, `NOTA_ACTUALIZAR`, `NOTA_ELIMINAR`, `NOTA_LECTURA` | CRUD | **Bloqueo automático** al cierre de bimestre. Modificación post-publicación requiere autorización ADMIN. |
| **Asistencia** | `ASISTENCIA_TOMAR`, `ASISTENCIA_MODIFICAR`, `ASISTENCIA_LECTURA` | CRUD | Corrección permitida hasta **48h** post-registro. |
| **Planificación** | `PLAN_CLASE_CRUD` | CRUD | Unidades didácticas, plan de clases. |
| **Comunicación** | `MENSAJE_ENVIAR_PADRE`, `MENSAJE_ENVIAR_ADMIN`, `MENSAJE_RECIBIR` | Envío/Lectura | Bandeja entrada/salida. |
| **Reportes** | `REPORTE_CURSO`, `REPORTE_ASISTENCIA` | Reporte | Solo sus cursos. |
| **Calendario** | `CALENDARIO_LEER` | Lectura | Calendario escolar oficial. |

#### ALUMNO
| Dimensión | Permisos | Tipo | Restricciones Críticas |
|-----------|----------|------|------------------------|
| **Perfil** | `PERFIL_LEER`, `PERFIL_EDITAR_LIMITADO` | Lectura/Actualización | Solo teléfono y foto. |
| **Académico** | `HORARIO_LEER`, `NOTA_LEER`, `ASISTENCIA_LEER`, `BOLETIN_LEER`, `BOLETIN_DESCARGAR`, `TAREA_LEER`, `MATERIAL_LEER`, `CALENDARIO_LEER` | Lectura | **NUNCA** ve datos de otros alumnos. |
| **Comunicación** | `MENSAJE_RECIBIR`, `MENSAJE_ENVIAR_DOCENTE`, `MENSAJE_ENVIAR_ADMIN` | Lectura/Envío | Solo a sus docentes/admin. |
| **Bloqueo Automático** | — | Sistema | Acceso **denegado** si estado ≠ `ACTIVO` (SUSPENDIDO, BAJA, RETIRADO, PRE_MATRICULADO, EGRESADO >90d, ARCHIVADO). |

#### PADRE DE FAMILIA
| Dimensión | Permisos | Tipo | Reglas de Negocio |
|-----------|----------|------|-------------------|
| **Perfil** | `PERFIL_LEER`, `PERFIL_EDITAR` | Lectura/Actualización | Teléfono, dirección, email. |
| **Hijos** | `HIJO_LISTAR`, `HIJO_DATOS_LEER`, `HIJO_HORARIO_LEER`, `HIJO_CURSO_LEER` | Lectura | Ve **TODOS** sus hijos (múltiples grados/niveles). Nunca ve hijos de otros. |
| **Académico Hijos** | `HIJO_NOTA_LEER`, `HIJO_ASISTENCIA_LEER`, `HIJO_BOLETIN_LEER`, `HIJO_BOLETIN_DESCARGAR`, `HIJO_REPORTE_RENDIMIENTO` | Lectura/Reporte | Acceso completo al historial de cada hijo. |
| **Financiero** | `PAGO_CONSULTAR`, `PAGO_REALIZAR`, `PAGO_HISTORIAL` | Lectura/Transacción | Estado de cuenta, cronograma, pago online. |
| **Matrícula** | `MATRICULA_SOLICITAR`, `MATRICULA_RENOVAR`, `RETIRO_SOLICITAR` | CRUD | Retiro requiere estar al corriente en pagos + firma digital. |
| **Comunicación** | `MENSAJE_RECIBIR`, `MENSAJE_ENVIAR_DOCENTE`, `MENSAJE_ENVIAR_ADMIN` | Lectura/Envío | Notificaciones automáticas + mensajería. |

---

## 2. CICLO DE VIDA DEL ALUMNO: MÁQUINA DE ESTADOS

### 2.1 Diagrama Formal de Transiciones

```
                    ┌───────────────────────┐
                    │   PRE-MATRICULADO      │
                    │   (Solicitud ingreso)  │
                    └───────────┬───────────┘
                               │ 1. Matrícula aprobada (ADMIN + docs OK)
                               │
                    ┌──────────▼───────────┐              ┌─────────────────┐
            ┌──────►│       ACTIVO         │◄─────────────│ SUSPENDIDO      │
            │       │    (Cursando)        │  7. Reinstal.│ (Restricción    │
            │       └──┬──┬─────┬──┬──────┘              │   temporal)     │
            │          │  │     │  │                      └──┬──────┬───────┘
            │          │  │     │  │                         │      │
            │          │  │     │  │       ┌─────────────────┘      │
            │          │  │     │  │       │                        │
            ▼          ▼  ▼     ▼  ▼       ▼                        ▼
     ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
     │ EGRESADO │ │ RETIRADO │ │  BAJA    │ │ARCHIVADO │ │ (Reingreso)  │
     │(Graduado)│ │(Volunta- │ │(Involun- │ │(Histórico│ │PRE_MATRICUL. │
     │          │ │  rio)    │ │  tario)  │ │ inactivo)│ │ >=6 meses    │
     └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────────┘
```

### 2.2 Definición Detallada de Estados

| Código | Nombre | Naturaleza | Duración Máx | Acceso Alumno | Acceso Padre | Sub-estados (Solo ACTIVO) |
|--------|--------|------------|--------------|---------------|--------------|---------------------------|
| `PRE_MATRICULADO` | Pre-matriculado | Transitorio | 30 días cal. | ❌ Ninguno | "Solicitud en proceso" | — |
| `ACTIVO` | Activo (Cursando) | Base | Indefinida | ✅ Completo | ✅ Completo | REGULAR, OBSERVADO_ACADEMICO (prom<12 o 2+ jaladas), OBSERVADO_CONDUCTUAL (2+ reportes) |
| `SUSPENDIDO` | Suspendido | Transitorio | 15 días hábiles (prorrogable 15+) | ❌ Bloqueado | Limitado (estado, notas antiguas, comunica) | ACADEMICA, DISCIPLINARIA, CONDUCTUAL, PREVENTIVA |
| `EGRESADO` | Egresado | **Terminal** | Permanente | Solo descarga docs (90 días) | Solo descarga docs (90 días) | — |
| `RETIRADO` | Retirado Voluntario | Terminal (reingreso condicionado) | Permanente | ❌ Ninguno | 30 días descarga docs | Causas: VOLUNTARIO, TRASLADO, SALUD, ECONOMICO, ADMINISTRATIVO |
| `BAJA` | Baja Involuntaria | Terminal (sin reingreso 2 años) | Permanente | ❌ Ninguno | ❌ Ninguno | Causas: EXPULSION, ABANDONO, DESISTIMIENTO, FALLECIMIENTO, SUS_VENCIDA |
| `ARCHIVADO` | Archivado | **Terminal Final** | Permanente | ❌ Ninguno | ❌ Ninguno (trámite admin) | Historial inmutable. |

### 2.3 Reglas de Negocio de la Máquina de Estados

1. **Inmutabilidad de estados terminales:** EGRESADO, BAJA y RETIRADO son terminales. No se puede retroceder.
2. **Reingreso controlado:** RETIRADO → PRE-MATRICULADO requiere 6 meses de espera + aprobación ADMIN.
3. **Trazabilidad total:** Cada cambio de estado se registra con usuario, fecha, motivo y documento soporte (tabla `historial_estados_alumno`).
4. **Notificación obligatoria:** Cualquier cambio de estado debe notificar al padre de familia (email, app, SMS).
5. **Suspensión progresiva:** No se puede expulsar (BAJA) sin pasar por SUSPENDIDO primero, excepto por causas gravísimas predefinidas (violencia, drogas, acoso sexual).
6. **Acceso condicional por estado:**
   - `ACTIVO` → Acceso completo.
   - `SUSPENDIDO` → Sin acceso para el alumno; padre ve estado.
   - `EGRESADO` → Acceso limitado para descarga de documentos (90 días).
   - `RETIRADO` → Solo padre por 30 días para descarga.
   - `BAJA` → Sin acceso para nadie.
   - `PRE-MATRICULADO` → Sin acceso para alumno; padre ve estado de solicitud.
7. **Suspensión automática por inasistencia:** Si un alumno ACTIVO acumula >20% de inasistencias en el período, transiciona automáticamente a SUSPENDIDO.
8. **Suspensión vencida:** Si SUSPENDIDO excede 30 días continuos sin reactivación → BAJA automática.
9. **Egreso automático:** Al fin del ciclo, si el alumno cumple todas las condiciones (materias aprobadas, asistencia ≥80%, pagos al día, documentos entregados) → EGRESADO automático a los 5 días del cierre del período.

### 2.4 Tipos de Suspensión (SUSPENDIDO)

| Tipo | Código | Descripción | Puede Reactivar |
|:-----|:-------|:------------|:---------------:|
| Académica | `SUS_ACADEMICA` | Bajo rendimiento sostenido | Sí (examen recuperación) |
| Disciplinaria | `SUS_DISCIPLINARIA` | Falta grave: bullying, agresión, daños | Sí (cumplimiento + carta) |
| Conductual | `SUS_CONDUCTUAL` | Incumplimiento reiterado de normas | Sí (compromiso firmado) |
| Preventiva | `SUS_PREVENTIVA` | Investigación por falta gravísima | Sí (si se descarta) |

### 2.5 Tabla de Transiciones (Resumen Técnico Ejecutable)

| # | Origen | Destino | Gatillante | ¿Automático? | ¿Requiere ADMIN? | ¿Requiere Consejo? | Notifica Padre | Plazo |
|---|--------|---------|-----------|:------------:|:----------------:|:------------------:|:--------------:|:-----:|
| 1 | `PRE_MATRICULADO` | `ACTIVO` | Matrícula aprobada | No | Sí | No | Sí | — |
| 2 | `PRE_MATRICULADO` | `BAJA` | Vencimiento 30 días | **Sí** | No | No | Sí | 30 días |
| 3 | `ACTIVO` | `SUSPENDIDO` | Inasistencia >20% | **Sí** | No | No | Sí | Inmediato |
| 4 | `ACTIVO` | `SUSPENDIDO` | Falta disciplinaria | No | Sí | No | Sí | 48h post-comité |
| 5 | `ACTIVO` | `SUSPENDIDO` | Bajo rendimiento | **Sí** | Sí | No | Sí (aviso 5d) | — |
| 6 | `ACTIVO` | `EGRESADO` | Fin ciclo + acreditación | **Sí** | Sí (validación final) | No | Sí | 5d post-cierre |
| 7 | `ACTIVO` | `RETIRADO` | Solicitud padre | No | Sí | No | Sí (padre solicita) | — |
| 8 | `ACTIVO` | `BAJA` | Abandono 30+ días | **Sí** | No | No | Sí | 30 días |
| 9 | `ACTIVO` | `BAJA` | Expulsión | No | Sí | **Sí** | Sí | 10d (invest.) |
| 10 | `SUSPENDIDO` | `ACTIVO` | Reinstalación | No | Sí | No | Sí | — |
| 11 | `SUSPENDIDO` | `RETIRADO` | Solicitud padre | No | Sí | No | No (padre solicita) | — |
| 12 | `SUSPENDIDO` | `BAJA` | Vencimiento 30d | **Sí** | No | No | Sí | 30 días |
| 13 | `SUSPENDIDO` | `BAJA` | Expulsión confirmada | No | Sí | **Sí** | Sí | — |
| 14 | `EGRESADO` | `ARCHIVADO` | Post 90 días | **Sí** | No | No | No | 90 días |
| 15 | `RETIRADO` | `ARCHIVADO` | Post 30 días | **Sí** | No | No | No | 30 días |
| 16 | `RETIRADO` | `PRE_MATRICULADO` | Reingreso (≥6 meses) | No | Sí | No | Sí | — |
| 17 | `BAJA` | `ARCHIVADO` | Post 2 años | **Sí** | No | No | No | 2 años |

---

## 3. HISTORIAS DE USUARIO CON CRITERIOS DE ACEPTACIÓN (GWT)

### MÓDULO 1: GESTIÓN DE CURSOS Y HORARIOS

#### HU-01: Configurar Plan de Estudios del Período

**Como** Administrador  
**Quiero** configurar el plan de estudios para un nuevo período académico  
**Para** definir qué grados y secciones se ofrecerán y qué materias los componen

**Criterios de Aceptación:**

**Escenario 1: Configuración exitosa del plan de estudios**
```
Given que soy un Administrador autenticado
  And que existe un período académico con estado "PLANIFICACION"
When selecciono el grado "5to Secundaria"
  And agrego las materias: "Matemática", "Comunicación", "Ciencias Sociales"
  And asigno 4 horas semanales a cada materia
Then el sistema guarda el plan de estudios
  And muestra la vista previa del plan con las 3 materias
  And el plan queda en estado "BORRADOR"
```

**Escenario 2: Validación de solapamiento de horas por grado**
```
Given que el grado "5to Secundaria - A" ya tiene 30 horas semanales asignadas
When intento agregar la materia "Educación Física" con 4 horas semanales
Then el sistema rechaza la operación
  And muestra el mensaje: "El total de horas semanales (34) excede el límite permitido (33)"
  And no modifica el plan actual
```

**Escenario 3: Activación del plan después de revisión**
```
Given que el plan de estudios está en estado "BORRADOR"
  And todas las materias tienen docente asignado
  And el total de horas no excede el límite
When confirmo el plan de estudios
Then el plan cambia a estado "ACTIVO"
  And ya no permite modificaciones sin desbloquearlo
  And se notifica a los docentes asignados
```

---

#### HU-02: Asignar Aula y Docente a Curso

**Como** Administrador  
**Quiero** asignar un docente y un aula a cada curso-materia del horario  
**Para** distribuir los recursos del colegio de forma óptima

**Criterios de Aceptación:**

**Escenario 1: Asignación exitosa sin conflictos**
```
Given que existe el curso "Matemática - 5to A" con 4 horas semanales
  And existe el docente "Juan Pérez" disponible 4 horas
  And existe el aula "A-101" disponible en los horarios requeridos
When asigno al docente Juan Pérez al curso
  And asigno el aula A-101 para las sesiones
Then el sistema valida que no hay conflictos de horario
  And guarda la asignación exitosamente
  And muestra el horario actualizado del docente y del aula
```

**Escenario 2: Conflicto de horario del docente**
```
Given que el docente "Juan Pérez" ya tiene asignado el curso "Comunicación - 5to A"
  And ese curso ocupa los bloques: Lunes 8-9am, Miércoles 8-9am
When intento asignar al mismo docente el curso "Matemática - 5to B"
  And ese curso requiere los bloques: Lunes 8-9am, Viernes 10-11am
Then el sistema detecta el conflicto en Lunes 8-9am
  And rechaza la asignación
  And muestra: "El docente ya tiene una asignación en Lunes 8:00 - 9:00 am (Comunicación - 5to A)"
  Y sugiere horarios alternativos disponibles para el docente
```

**Escenario 3: Conflicto de aula compartida**
```
Given que el aula "Lab-101" está asignada a "Ciencias - 5to A" los Martes 10-12am
When intento asignar el aula "Lab-101" a "Química - 5to B" los Martes 10-11am
Then el sistema detecta el conflicto en Martes 10-11am
  And rechaza la asignación
  And muestra: "El aula Lab-101 ya está reservada: Martes 10:00-12:00 (Ciencias - 5to A)"
```

**Escenario 4: Validación de carga horaria del docente**
```
Given que el docente "María López" ya tiene 24 horas semanales asignadas
  And el límite máximo es 26 horas semanales
When intento asignarle "Arte - 1ro A" que requiere 3 horas semanales
Then el sistema rechaza la asignación
  And muestra: "La carga horaria del docente sería 27h, superando el límite de 26h semanales"
```

---

#### HU-03: Generar Horario Automático con Detección de Colisiones

**Como** Administrador  
**Quiero** que el sistema genere un horario automático respetando todas las restricciones de asignación  
**Para** ahorrar tiempo y evitar errores manuales

**Criterios de Aceptación:**

**Escenario 1: Generación de horario sin colisiones**
```
Given que existen 3 grados (1ro, 2do, 3ro Secundaria)
  And cada grado tiene 2 secciones (A, B)
  And hay 12 docentes asignados a las materias
  And hay 8 aulas disponibles
  And todos los datos de asignación docente-aula están completos
When ejecuto "Generar horario automático"
Then el sistema genera un horario en menos de 30 segundos
  And verifica que no hay colisiones de: docente, aula, grado
  And muestra el horario en vista semanal por grado/sección
  And cada bloque tiene: materia, docente, aula
```

**Escenario 2: No se puede generar el horario por restricciones**
```
Given que hay 2 materias que requieren el mismo docente en el mismo bloque horario
  And no hay otro docente disponible para esas materias
When ejecuto "Generar horario automático"
Then el sistema no genera el horario
  And muestra un reporte de conflictos irresolubles
  And lista: "Conflicto: Matemática - 5to A y 5to B requieren a Juan Pérez los Lunes 8-9am"
  And sugiere: "Asignar un docente adicional para una de las secciones"
```

**Escenario 3: Vista previa y ajuste manual del horario generado**
```
Given que el horario ha sido generado automáticamente
When selecciono la vista por "Docente: Juan Pérez"
Then veo una tabla con los días de la semana (Lun-Vie) y horas (8am-3pm)
  And cada celda ocupada muestra: "Matemática - 5to A - Aula 101"
  And las celdas libres están en blanco
  And puedo arrastrar y soltar un bloque a otra celda libre
  And al soltar, el sistema revalida que no hay colisiones
  And si hay colisión, revierte el cambio y muestra alerta
```

---

### MÓDULO 2: REGISTRO DE ALUMNOS Y DOCENTES

#### HU-04: Registro Masivo de Alumnos desde Archivo

**Como** Administrador  
**Quiero** poder cargar alumnos desde un archivo Excel o CSV  
**Para** agilizar el proceso de matrícula al inicio del año

**Criterios de Aceptación:**

**Escenario 1: Carga exitosa de archivo con datos válidos**
```
Given que tengo un archivo Excel con 50 alumnos (columnas: DNI, nombres, apellidos, fecha_nac, grado)
  And el archivo tiene el formato correcto según la plantilla oficial
When subo el archivo al sistema
Then el sistema procesa los 50 registros
  And muestra un resumen: "50 alumnos creados exitosamente"
  And cada alumno tiene:
    - Código de estudiante auto-generado (formato: AÑO-SECUENCIAL)
    - Estado "PRE_MATRICULADO"
    - Credenciales generadas e importadas
  And se genera un reporte de importación en PDF
```

**Escenario 2: Archivo con datos duplicados**
```
Given que en el archivo hay 3 registros con DNI que ya existen en el sistema
  And hay 47 registros nuevos válidos
When proceso el archivo
Then el sistema crea los 47 alumnos nuevos
  And rechaza los 3 duplicados
  And muestra: "3 registros omitidos por DNI duplicado: 12345678, 23456789, 34567890"
  And el reporte incluye la lista de duplicados con su código existente
```

**Escenario 3: Archivo con errores de formato**
```
Given que el archivo tiene una columna "edad" en vez de "fecha_nacimiento"
When intento subir el archivo
Then el sistema rechaza el archivo antes de procesarlo
  And muestra: "Error de formato: no se encontró la columna requerida 'fecha_nacimiento'"
  And descarga la plantilla oficial con las columnas correctas
```

---

#### HU-05: Registro Individual de Docente

**Como** Administrador  
**Quiero** registrar un nuevo docente con todos sus datos contractuales  
**Para** que pueda acceder al sistema y se le asignen cursos

**Criterios de Aceptación:**

**Escenario 1: Registro exitoso de docente**
```
Given que estoy en el formulario "Nuevo Docente"
When ingreso:
  | Campo | Valor |
  | DNI | 12345678 |
  | Nombres | "María Elena" |
  | Apellidos | "García López" |
  | Email | maria.garcia@colegio.edu |
  | Teléfono | 999888777 |
  | Especialidad | "Matemática" |
  | Fecha de ingreso | "01-03-2025" |
  | Tipo de contrato | "PLANILLA" |
  | Carga horaria máxima | 26 |
And hago clic en "Guardar"
Then el sistema registra al docente
  And genera:
    - Código de docente: PROF-2025-001
    - Usuario: mgarcia (generado automáticamente)
    - Contraseña temporal: enviada al email registrado
  And el estado del docente es "ACTIVO"
  And me redirige al perfil del docente creado
```

**Escenario 2: DNI o email ya registrado**
```
Given que ya existe un docente con DNI 12345678
When intento registrar un nuevo docente con ese mismo DNI
Then el sistema rechaza el registro
  And muestra: "El DNI 12345678 ya está registrado como docente: María García"
  And si quiero, puedo ver el perfil existente desde el mensaje
```

**Escenario 3: Campos obligatorios incompletos**
```
Given que estoy en el formulario "Nuevo Docente"
When intento guardar sin completar el email
Then el sistema resalta el campo email en rojo
  And muestra: "El email es obligatorio"
  And no guarda el registro
```

---

#### HU-06: Asociar Padre de Familia a Alumno

**Como** Administrador  
**Quiero** asociar uno o varios padres de familia a un alumno  
**Para** que los padres puedan dar seguimiento académico

**Criterios de Aceptación:**

**Escenario 1: Asociar padre existente a alumno**
```
Given que existe el alumno "Carlos López" (código AL-2025-001)
  And existe el padre "Pedro López" con DNI 87654321
When busco al padre por DNI en la sección "Asociar Padre"
  And selecciono al padre encontrado
  And marco el parentesco como "PADRE"
Then el sistema asocia al padre con el alumno
  And el padre ahora puede ver los datos del alumno en su dashboard
  And el alumno muestra "Padre: Pedro López" en su perfil
  And se envía notificación al padre: "Has sido asociado a Carlos López"
```

**Escenario 2: Registrar y asociar padre nuevo**
```
Given que estoy registrando un alumno nuevo
  And el padre no existe en el sistema
When en el formulario del alumno, marco "Registrar padre nuevo"
  And completo: DNI, nombres, apellidos, email, teléfono, parentesco
Then el sistema crea el padre como nuevo usuario
  And genera credenciales de acceso (enviadas por email)
  And asocia al padre con el alumno inmediatamente
  Y el alumno queda registrado con padre asociado
```

**Escenario 3: Múltiples padres para un alumno**
```
Given que el alumno "Carlos López" ya tiene asociado a "Pedro López" como PADRE
When asocio también a "María López" como MADRE
Then el alumno muestra ambos padres en su perfil
  And ambos padres tienen acceso completo al perfil del alumno
  And el sistema registra ambos con su parentesco respectivo
```

---

### MÓDULO 3: SUBIDA Y VISUALIZACIÓN DE CALIFICACIONES

#### HU-07: Registrar Notas por Bimestre/Período

**Como** Docente  
**Quiero** registrar las notas de mis alumnos organizadas por bimestre y tipo de evaluación  
**Para** llevar un control académico estructurado

**Criterios de Aceptación:**

**Escenario 1: Registro exitoso de notas en un bimestre**
```
Given que soy el docente "Juan Pérez"
  And tengo asignado el curso "Matemática - 3ro A"
  And el bimestre "I Bimestre" está abierto para registro (01-04-2025 al 30-04-2025)
When accedo al registro de notas del curso
  And veo la lista de 25 alumnos con sus nombres
  And selecciono el tipo de evaluación: "Examen Parcial" (peso 30%)
  And ingreso notas para los 25 alumnos en escala 0-20
Then el sistema guarda las notas
  And calcula automáticamente el promedio ponderado de cada alumno
  And muestra la columna "Promedio Parcial" actualizada
  And muestra un indicador visual por alumno:
    - Nota ≥ 14 (Aprobado - verde)
    - Nota entre 11 y 13 (En observación - ámbar)
    - Nota < 11 (Desaprobado - rojo)
```

**Escenario 2: Validación de rango de notas**
```
Given que estoy registrando notas de "Matemática - 3ro A"
When intento ingresar una nota de "25" para el alumno "Carlos López"
Then el sistema no acepta el valor
  And muestra: "La nota debe estar entre 0 y 20"
  And el campo se resalta en rojo
  And el resto de notas guardadas no se afectan
```

**Escenario 3: Cierre de bimestre y bloqueo de edición**
```
Given que la fecha actual es 02-05-2025
  And el bimestre "I Bimestre" cerró el 30-04-2025
When intento modificar una nota registrada
Then el sistema bloquea la edición
  And muestra: "El I Bimestre está cerrado. Para modificaciones, solicite apertura al Administrador"
  And la vista cambia a solo lectura
```

**Escenario 4: Publicación de notas al padre**
```
Given que he registrado todas las notas del I Bimestre
When hago clic en "Publicar Notas"
Then el sistema cambia el estado del bimestre a "PUBLICADO"
  And los padres de los 25 alumnos pueden ver las notas en su dashboard
  And los alumnos pueden ver sus propias notas
  And se envía notificación: "Notas del I Bimestre disponibles - Matemática"
  And el botón de edición se desactiva (solo ADMIN puede reabrir)
```

---

#### HU-08: Visualizar Reporte de Notas por Alumno

**Como** Padre de Familia  
**Quiero** ver las notas de mi hijo desglosadas por curso y bimestre  
**Para** dar seguimiento a su rendimiento académico

**Criterios de Aceptación:**

**Escenario 1: Visualización del reporte de notas**
```
Given que soy "Pedro López", padre de "Carlos López" (5to Secundaria)
When accedo al perfil de mi hijo
  And selecciono la pestaña "Notas"
Then veo una tabla con:
  - Columnas: Curso | I Bim | II Bim | III Bim | IV Bim | Promedio Final
  - Filas por cada curso: Matemática, Comunicación, Ciencias, etc.
  - Cada nota con su color de aprobación (verde ≥14, amarillo 11-13, rojo <11)
  - Una fila de "Promedio General" al final
  - Un indicador visual del rendimiento general
```

**Escenario 2: Detalle por curso y bimestre**
```
Given que veo el reporte de notas de mi hijo
When hago clic en "Matemática - I Bimestre"
Then veo el detalle:
  - Desglose de evaluaciones: Parcial (30%), Trabajo (20%), Examen Final (50%)
  - Nota individual de cada evaluación
  - Fecha de cada evaluación
  - Promedio ponderado del bimestre: 15.5
  - Comentario del docente: "Buen desempeño, sugerimos reforzar geometría"
```

**Escenario 3: Comparativa histórica de bimestres**
```
Given que estoy en la vista de notas de mi hijo
When selecciono la opción "Ver tendencia"
Then veo un gráfico de líneas por cada curso
  - Eje X: I Bim, II Bim, III Bim, IV Bim
  - Eje Y: Nota (0-20)
  - Una línea por cada curso
  - Una línea horizontal en 14 (nota mínima aprobatoria)
  - Tooltip al pasar el mouse: "Matemática - III Bim: 12.5 (debajo del mínimo)"
```

---

#### HU-09: Carga Masiva de Notas desde Excel

**Como** Docente  
**Quiero** subir las notas de todo un curso mediante un archivo Excel  
**Para** ahorrar tiempo cuando tengo muchos alumnos

**Criterios de Aceptación:**

**Escenario 1: Carga masiva exitosa**
```
Given que tengo un archivo Excel con notas de "Matemática - 3ro A"
  And el archivo tiene: código_alumno, examen_parcial, trabajo, examen_final
  And todos los códigos de alumno existen en el curso
When subo el archivo
Then el sistema procesa 25 registros
  And muestra resumen: "25 notas cargadas exitosamente"
  Y calcula los promedios automáticamente
  Y puedo revisar las notas en pantalla antes de publicar
```

**Escenario 2: Archivo con códigos de alumno inválidos**
```
Given que el archivo contiene 2 códigos de alumno que no pertenecen al curso
When subo el archivo
Then el sistema muestra: "2 registros ignorados: AL-2025-999 y AL-2025-888 no pertenecen a este curso"
  And carga las 23 notas válidas
  And me permite descargar un reporte de errores
  Y corregir y reintentar los registros fallidos
```

---

### MÓDULO 4: CONTROL DE ASISTENCIA DIARIA

#### HU-10: Registro Rápido de Asistencia

**Como** Docente  
**Quiero** registrar la asistencia de mis alumnos en cada sesión de clase de forma rápida  
**Para** tener un control actualizado de las inasistencias

**Criterios de Aceptación:**

**Escenario 1: Registro de asistencia por defecto (todos presentes)**
```
Given que soy el docente de "Matemática - 3ro A"
  And la hora actual es 8:00 am (inicio de clase)
  And la sesión es: Lunes 8-9am, Aula 101
When accedo al registro de asistencia
Then veo la lista de 25 alumnos con todas las casillas marcadas como "PRESENTE"
  Y puedo cambiar individualmente a:
    - "FALTA"
    - "TARDANZA" (con selector de minutos: 5, 10, 15, 20, 30)
    - "JUSTIFICADO"
    - "LICENCIA"
  Y un contador: "Presentes: 25 | Faltas: 0 | Tardanzas: 0"
```

**Escenario 2: Corrección rápida por lotes**
```
Given que estoy registrando asistencia
When marco la casilla de "Todos Presentes" (default)
  Y luego marco 3 alumnos como "FALTA" con un clic cada uno
Then el contador se actualiza a: "Presentes: 22 | Faltas: 3 | Tardanzas: 0"
  Y cada alumno con falta se resalta en rojo en la lista
  Y se muestra la fecha y hora del registro
```

**Escenario 3: Confirmación y cierre de asistencia**
```
Given que he marcado todas las asistencias
When hago clic en "Confirmar Asistencia"
Then el sistema guarda el registro con timestamp
  Y muestra: "Asistencia registrada: Lunes 15-04-2025 8:00-9:00 - Matemática 3ro A"
  Y ya no permite modificar la sesión (solo hasta 48h después, con autorización)
  Y si intento modificar después de 48h, muestra: "Período de corrección vencido. Contacte al Administrador"
```

---

#### HU-11: Alerta de Faltas Consecutivas (Regla de Negocio Crítica)

**Como** Administrador  
**Quiero** recibir una alerta automática cuando un alumno acumule 3 o más faltas consecutivas  
**Para** intervenir oportunamente con el padre de familia

**Regla de Negocio (Lógica de Alerta):**
1. Se considera "falta consecutiva" cuando un alumno registra `FALTA` en **todas** las sesiones de un mismo curso en días de clase consecutivos.
2. `JUSTIFICADO` y `LICENCIA` **no** cuentan como falta consecutiva (rompen la secuencia).
3. Si hay `PRESENTE` o `TARDANZA` entre dos `FALTA`, la secuencia se reinicia.
4. Al confirmarse la 3ª falta consecutiva → alerta de nivel `ALTA` (ámbar).
5. Al confirmarse la 5ª falta consecutiva → alerta de nivel `CRÍTICA` (rojo) + escalamiento a Director Académico + sugerencia de suspensión.
6. La alerta se dispara **al momento de confirmar la sesión de clase** (evento asíncrono `AsistenciaConfirmadaEvent`).
7. Las alertas tienen 3 estados: `NUEVA`, `VISTA`, `RESUELTA`.

**Criterios de Aceptación:**

**Escenario 1: Detección de 3 faltas consecutivas**
```
Given que el alumno "Carlos López" tiene registrado:
  - Lunes 01-04: FALTA (Matemática)
  - Miércoles 03-04: FALTA (Matemática)
  - Viernes 05-04: FALTA (Matemática)
When el docente confirma la asistencia del viernes 05-04
Then el sistema detecta automáticamente 3 faltas consecutivas en el mismo curso
  Y genera una alerta en el dashboard del Administrador
  Y envía una notificación al padre: "Su hijo Carlos López tiene 3 faltas consecutivas en Matemática"
  Y la alerta muestra:
    - Alumno: Carlos López
    - Curso: Matemática - 3ro A
    - Faltas: 3 (01/04, 03/04, 05/04)
    - Estado: "NUEVA"
```

**Escenario 2: Alerta en el dashboard del docente**
```
Given que el alumno "Carlos López" tiene 3 faltas consecutivas en Matemática
When el docente "Juan Pérez" accede a su dashboard
Then ve un widget de "Alertas de Asistencia"
  Y el widget muestra: "1 alumno con 3+ faltas consecutivas - Matemática 3ro A"
  Y al hacer clic, ve el detalle del alumno y las fechas
  Y un botón: "Notificar al padre" (envía mensaje rápido)
```

**Escenario 3: Escalamiento a 5 faltas consecutivas**
```
Given que el alumno ya tiene 4 faltas consecutivas registradas
When se registra la quinta falta consecutiva
Then el sistema:
  - Cambia el nivel de la alerta a "CRÍTICA"
  - Notifica al Administrador y al Director Académico
  - Sugiere acción: "Evaluar suspensión por inasistencia (>20%)"
  - El alumno se marca como candidato a revisión de estado
  - Se programa una reunión automática con el padre
```

**Escenario 4: Faltas no consecutivas (no genera alerta)**
```
Given que el alumno "Ana Martínez" tiene asistencias intercaladas:
  - Lunes 01-04: PRESENTE
  - Miércoles 03-04: FALTA
  - Viernes 05-04: PRESENTE
  - Lunes 08-04: FALTA
When se registra la asistencia del lunes 08-04
Then el sistema NO genera alerta de faltas consecutivas
  Y solo actualiza el contador general de faltas del alumno (2 faltas en total)
```

---

#### HU-12: Reporte de Asistencia por Período

**Como** Administrador  
**Quiero** generar un reporte de asistencia de un alumno o curso en un rango de fechas  
**Para** evaluar el comportamiento de asistencia y tomar decisiones

**Criterios de Aceptación:**

**Escenario 1: Reporte de asistencia de un alumno**
```
Given que estoy en la sección de reportes
When selecciono "Reporte de Asistencia por Alumno"
  Y busco "Carlos López" (AL-2025-001)
  Y selecciono rango: "01-03-2025 al 31-03-2025"
Then el sistema genera un reporte con:
  - Alumno: Carlos López - 5to Secundaria A
  - Período: Marzo 2025
  - Total de sesiones: 20
  - Presente: 15 (75%)
  - Falta: 3 (15%)
  - Tardanza: 2 (10%)
  - Justificado: 0
  - Desglose día por día con estado
  - Porcentaje de asistencia: 85%
  - Indicador (dentro del mínimo de 80%)
```

**Escenario 2: Reporte de asistencia por curso**
```
Given que estoy generando un reporte
When selecciono "Reporte de Asistencia por Curso"
  Y elijo "Matemática - 3ro A"
  Y período: "I Bimestre"
Then el sistema genera un reporte con:
  - Curso: Matemática - 3ro A
  - Docente: Juan Pérez
  - Período: I Bimestre (01-03 al 30-04)
  - Tabla con:
    | Alumno | Sesiones | Presente | Falta | Tardanza | % Asistencia |
    |--------|----------|----------|-------|----------|--------------|
    | Carlos López | 16 | 12 | 3 | 1 | 81% |
    | Ana Martínez | 16 | 15 | 1 | 0 | 96% |
  - Promedio de asistencia del curso: 92%
  - Alumnos debajo del 80%: 2 (Carlos López, Pedro Ruiz)
  - Botón de exportar
```

---

### MÓDULO 5: REPORTES Y EXPORTACIÓN

#### HU-13: Exportar Libreta de Notas a PDF

**Como** Administrador  
**Quiero** exportar la libreta de notas de un alumno o de todo un curso en formato PDF  
**Para** entregar boletines físicos o digitales a los padres

**Criterios de Aceptación:**

**Escenario 1: Exportar libreta de un alumno**
```
Given que estoy en el perfil del alumno "Carlos López"
When selecciono "Exportar Libreta" > "PDF"
  Y elijo "I Bimestre 2025"
Then el sistema genera un PDF con:
  - Header: Logo del colegio, nombre, dirección, RUC
  - Datos del alumno: nombre completo, código, grado, sección
  - Período: I Bimestre 2025
  - Tabla de notas: Curso | Docente | Parcial(30%) | Trabajo(20%) | Ex.Final(50%) | Promedio
  - Promedio general
  - Estado: "APROBADO" / "EN OBSERVACIÓN" / "DESAPROBADO"
  - Fecha de emisión
  - Código QR de verificación
  - Firma digital del director
```

**Escenario 2: Exportar libreta de todo un curso**
```
Given que estoy en la vista del curso "Matemática - 3ro A"
When selecciono "Exportar Libreta Curso" > "PDF"
  Y elijo "I Bimestre 2025"
Then el sistema genera un PDF con:
  - Portada: Curso, Docente, Período
  - Tabla consolidada (25 alumnos): # | Código | Alumno | Parcial | Trabajo | Ex.Final | Prom.
  - Resumen: aprobados (20), observados (3), desaprobados (2)
  - Orientación horizontal (Apaisada)
```

**Escenario 3: Personalización del PDF**
```
Given que voy a exportar una libreta
When selecciono "Opciones de exportación"
Then puedo elegir:
  - [x] Incluir logo del colegio
  - [x] Incluir firma digital
  - [ ] Incluir código QR
  - [x] Mostrar promedios ponderados
  - Formato: [PDF] | [Excel]
```

---

#### HU-14: Exportar Horario a PDF o Excel

**Como** Docente  
**Quiero** exportar mi horario semanal o el de un curso en PDF o Excel  
**Para** imprimirlo o compartirlo

**Criterios de Aceptación:**

**Escenario 1: Exportar horario del docente**
```
Given que soy el docente "Juan Pérez"
When selecciono "Mi Horario" > "Exportar" > "PDF"
Then el sistema genera un PDF con:
  - Header: "Horario - Juan Pérez - 2025"
  - Tabla semanal (Lun-Vie, 8:00-15:00):
    | Hora   | Lunes          | Martes         | Miércoles      |
    |--------|----------------|----------------|----------------|
    | 8-9    | Mate 5to A-101 | Mate 5to B-102 | Mate 5to A-101 |
    | 9-10   | Mate 5to B-102 |                | Mate 5to B-102 |
  - Leyenda: Aula, Curso
  - Generado: 15-04-2025
```

**Escenario 2: Exportar horario en Excel editable**
```
Given que estoy viendo el horario del curso "5to Secundaria A"
When selecciono "Exportar" > "Excel (.xlsx)"
Then el sistema descarga un archivo Excel con:
  - Hoja "Horario 5to A" con el formato de tabla semanal
  - Celdas con bordes y colores por materia
  - Filtros activos por columna
  - El archivo es editable (no protegido)
```

---

#### HU-15: Reporte de Rendimiento por Curso y Período

**Como** Director Académico (rol: ADMIN)  
**Quiero** generar un reporte estadístico de rendimiento por curso y bimestre  
**Para** identificar tendencias, materias críticas y tomar decisiones curriculares

**Criterios de Aceptación:**

**Escenario 1: Reporte de rendimiento de un grado**
```
Given que soy Director Académico
When selecciono "Reportes" > "Rendimiento por Grado"
  Y elijo "5to Secundaria" > "Todos los bimestres 2025"
Then el sistema genera un reporte con:
  - Grado: 5to Secundaria (A y B)
  - Período: 2025 (I, II, III, IV Bimestre)
  - Gráfico de barras por curso
  - Métricas:
    - Materia con mejor rendimiento: Comunicación (15.2)
    - Materia con peor rendimiento: Matemática (13.7)
    - Tendencia: 3 de 5 materias mejoraron vs año anterior
  - Lista de alumnos con promedio < 11 (zona crítica): 4 alumnos
```

**Escenario 2: Exportar reporte a PDF con gráficos**
```
Given que tengo el reporte generado en pantalla
When selecciono "Exportar PDF"
Then el PDF incluye:
  - Portada institucional
  - Tablas y gráficos embebidos (barras, líneas de tendencia)
  - Resumen ejecutivo en la primera página
  - Anexo: lista detallada de alumnos críticos
  - El archivo pesa menos de 5MB
  - Formato: A4, orientación vertical
```

---

#### HU-16: Dashboard de Alertas y Métricas Clave

**Como** Administrador  
**Quiero** un dashboard con métricas clave y alertas activas  
**Para** tener visibilidad inmediata del estado del colegio

**Criterios de Aceptación:**

**Escenario 1: Dashboard al iniciar sesión**
```
Given que soy Administrador
When inicio sesión
Then veo mi dashboard con los siguientes widgets:

  ┌─────────────────────────────────────────────────────────────────────┐
  │                     RESUMEN GENERAL                                │
  │  Total alumnos: 450  |  Activos: 430  |  Suspendidos: 8           │
  │  Total docentes: 35   |  Cursos activos: 80                        │
  └─────────────────────────────────────────────────────────────────────┘
  ┌─────────────────────────────────────────────────────────────────────┐
  │                     ALERTAS ACTIVAS (5)                             │
  │  3 faltas consecutivas: 2 alumnos                                  │
  │  Suspensión pendiente: 1 alumno                                    │
  │  Docentes sin asignar: 2                                           │
  └─────────────────────────────────────────────────────────────────────┘
  ┌──────────────────────────┐  ┌──────────────────────────────────┐
  │  RENDIMIENTO PROMEDIO    │  │  ASISTENCIA GLOBAL (Mes Actual)  │
  │  [Gráfico: Barras x Curso] │  │  [Gráfico: Doughnut 92% / 8%]  │
  └──────────────────────────┘  └──────────────────────────────────┘
```

---

## 4. REGLAS DE NEGOCIO CRÍTICAS

### 4.1 Lógica para Evitar Colisiones de Horarios

**Regla fundamental:** No pueden existir dos bloques horarios que se superpongan en tiempo para el mismo **docente**, **aula** o **grado/sección**.

**Algoritmo de validación (aplicar en BACKEND en `HorarioService`):**

```
fun validarBloque(dto: BloqueHorarioDTO, bloqueExcluirId?: Long):
    errores = []

    // 1. Validar colisión de DOCENTE
    docenteDB = horarioBloqueRepository.findConflictos(
        docenteId = dto.docenteId,
        dia = dto.diaSemana,
        horaInicio = dto.horaInicio,
        horaFin = dto.horaFin,
        excludeBloqueId = bloqueExcluirId
    )
    si docenteDB.noEstáVacío():
        errores.agregar(ErrorColision(
            tipo = "DOCENTE",
            recurso = dto.docenteId,
            mensaje = "Docente ocupado: ${docenteDB.first().curso.materia.nombre} - ${docenteDB.first().curso.aula.nombre}"
        ))

    // 2. Validar colisión de AULA
    aulaDB = horarioBloqueRepository.findConflictos(
        aulaId = dto.aulaId, dia = dto.diaSemana,
        horaInicio = dto.horaInicio, horaFin = dto.horaFin,
        excludeBloqueId = bloqueExcluirId
    )
    si aulaDB.noEstáVacío():
        errores.agregar(ErrorColision(
            tipo = "AULA",
            recurso = dto.aulaId,
            mensaje = "Aula ocupada: ${aulaDB.first().curso.materia.nombre} - ${aulaDB.first().curso.docente.nombre}"
        ))

    // 3. Validar colisión de GRADO/SECCIÓN
    gradoSeccionDB = horarioBloqueRepository.findConflictos(
        gradoSeccionId = dto.curso.gradoSeccion.id,
        dia = dto.diaSemana, horaInicio = dto.horaInicio, horaFin = dto.horaFin,
        excludeBloqueId = bloqueExcluirId
    )
    si gradoSeccionDB.noEstáVacío():
        errores.agregar(ErrorColision(
            tipo = "GRADO_SECCION",
            recurso = dto.curso.gradoSeccion.id,
            mensaje = "El grado/sección ya tiene una clase en ese horario"
        ))

    // 4. Validar CARGA HORARIA DOCENTE (límite semanal)
    cargaActual = cursoRepository.findCargaHorariaDocente(dto.docenteId, periodoId)
    si (cargaActual + dto.horasSemanales) > docente.cargaHorariaMax:
        errores.agregar(ErrorColision(
            tipo = "CARGA_HORARIA",
            recurso = dto.docenteId,
            mensaje = "La carga horaria del docente sería ${cargaActual + dto.horas}h, superando el límite de ${docente.cargaHorariaMax}h semanales"
        ))

    si errores.noEstáVacío():
        throw BusinessRuleViolationException("COLISION_HORARIO", errores)
```

**Query SQL para detección de conflictos:**

```sql
SELECT hb.*, c.materia_id, c.docente_id, c.grado_seccion_id
FROM horario_bloque hb
JOIN curso c ON hb.curso_id = c.id
WHERE c.periodo_id = :periodoId
  AND hb.dia_semana = :dia
  AND hb.hora_inicio < :horaFin
  AND hb.hora_fin > :horaInicio
  AND (:excluirBloqueId IS NULL OR hb.id != :excluirBloqueId)
  AND (
      c.docente_id = :docenteId
      OR hb.aula_id = :aulaId
      OR c.grado_seccion_id = :gradoSeccionId
  )
```

### 4.2 Lógica de Alerta Automática por 3+ Faltas Consecutivas

**Regla fundamental:** Al confirmar una sesión de clase (evento `AsistenciaConfirmadaEvent`), el sistema debe verificar, para cada alumno marcado como `FALTA`, si acumula 3 o más faltas consecutivas en ese mismo curso.

**Algoritmo de detección (aplicar en BACKEND en `AsistenciaAlertListener`):**

```
fun detectarFaltasConsecutivas(evento: AsistenciaConfirmadaEvent):
    sesion = evento.sesion
    asistenciasFalta = sesion.asistencias.filtrar { it.tipoAsistencia.codigo == "FALTA" }

    por cada asistencia en asistenciasFalta:
        faltas = asistenciaRepository.findFaltasConsecutivas(
            alumnoId = asistencia.alumno.id,
            cursoId = sesion.curso.id,
            fechaReferencia = sesion.fecha,
            minConsecutivas = 3
        )
        // findFaltasConsecutivas devuelve lista ordenada DESC de sesiones
        // donde hubo FALTA, consecutivas, hasta encontrar PRESENTE/TARDANZA

        cantidad = faltas.size()
        si cantidad >= 3:
            nivel = si cantidad >= 5 entonces "CRITICA" sino "ALTA"

            alerta = AlertaFaltasConsecutivas(
                alumno = asistencia.alumno,
                curso = sesion.curso,
                cantidadConsecutivas = cantidad,
                fechas = faltas.map { it.fecha },
                nivel = nivel,
                estado = "NUEVA"
            )
            alertaRepository.save(alerta)

            // Notificar
            notificacionService.notificarPadre(
                padre = asistencia.alumno.padrePrincipal(),
                mensaje = "Su hijo ${alumno.nombre} tiene ${cantidad} faltas consecutivas en ${curso.materia.nombre}"
            )

            si cantidad >= 5:
                notificacionService.notificarAdmin(
                    mensaje = "ALERTA CRÍTICA: ${alumno.nombre} - ${cantidad} faltas consecutivas en ${curso.materia.nombre}. Evaluar suspensión."
                )
                // Disparar evento de sugerencia de suspensión
                eventPublisher.publishEvent(EvaluarSuspensionEvent(alumno, curso))
```

---

## 5. MODELO DE DATOS (ENTIDADES BASE)

```sql
-- CATÁLOGO DE ESTADOS DEL ALUMNO
CREATE TABLE estados_alumno (
    id              BIGSERIAL PRIMARY KEY,
    codigo          VARCHAR(30)  NOT NULL UNIQUE,
    nombre          VARCHAR(100) NOT NULL,
    descripcion     TEXT,
    es_terminal     BOOLEAN DEFAULT FALSE,
    es_transitorio  BOOLEAN DEFAULT FALSE,
    permiso_acceso  VARCHAR(20)  DEFAULT 'NINGUNO',
    activo          BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TRANSICIONES PERMITIDAS
CREATE TABLE transiciones_alumno (
    id                  BIGSERIAL PRIMARY KEY,
    estado_origen_id    BIGINT NOT NULL REFERENCES estados_alumno(id),
    estado_destino_id   BIGINT NOT NULL REFERENCES estados_alumno(id),
    codigo_gatillante   VARCHAR(50)  NOT NULL,
    descripcion         VARCHAR(255),
    es_automatica       BOOLEAN DEFAULT FALSE,
    requiere_admin      BOOLEAN DEFAULT TRUE,
    requiere_consejo    BOOLEAN DEFAULT FALSE,
    requiere_documento  BOOLEAN DEFAULT FALSE,
    notifica_padre      BOOLEAN DEFAULT TRUE,
    plazo_limite_dias   INTEGER,
    activo              BOOLEAN DEFAULT TRUE,
    UNIQUE(estado_origen_id, estado_destino_id, codigo_gatillante)
);

-- ALUMNO
CREATE TABLE alumnos (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL UNIQUE REFERENCES usuarios(id),
    codigo_estudiante   VARCHAR(20)  NOT NULL UNIQUE,
    nombres             VARCHAR(100) NOT NULL,
    apellidos           VARCHAR(100) NOT NULL,
    fecha_nacimiento    DATE,
    estado_actual_id    BIGINT NOT NULL REFERENCES estados_alumno(id),
    sub_estado          VARCHAR(30),
    fecha_ingreso       DATE NOT NULL,
    fecha_ultimo_estado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo              BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- HISTORIAL DE ESTADOS
CREATE TABLE historial_estados_alumno (
    id                  BIGSERIAL PRIMARY KEY,
    alumno_id           BIGINT NOT NULL REFERENCES alumnos(id),
    transicion_id       BIGINT REFERENCES transiciones_alumno(id),
    estado_anterior_id  BIGINT REFERENCES estados_alumno(id),
    estado_nuevo_id     BIGINT NOT NULL REFERENCES estados_alumno(id),
    usuario_id          BIGINT REFERENCES usuarios(id),
    motivo              TEXT NOT NULL,
    documento_url       VARCHAR(500),
    fecha_cambio        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vigencia_hasta      DATE
);

-- PERÍODO ACADÉMICO
CREATE TABLE periodos_academicos (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    codigo      VARCHAR(20) NOT NULL UNIQUE,
    fecha_inicio DATE NOT NULL,
    fecha_fin   DATE NOT NULL,
    estado      VARCHAR(30) DEFAULT 'PLANIFICACION',
    activo      BOOLEAN DEFAULT TRUE
);

-- GRADO
CREATE TABLE grados (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    nivel           VARCHAR(30) NOT NULL,
    orden           INTEGER NOT NULL,
    capacidad_max   INTEGER
);

-- SECCIÓN
CREATE TABLE secciones (
    id        BIGSERIAL PRIMARY KEY,
    grado_id  BIGINT NOT NULL REFERENCES grados(id),
    nombre    VARCHAR(50) NOT NULL,
    capacidad INTEGER
);

-- MATERIA
CREATE TABLE materias (
    id                    BIGSERIAL PRIMARY KEY,
    nombre                VARCHAR(100) NOT NULL,
    codigo                VARCHAR(20) NOT NULL UNIQUE,
    horas_semanales_req   INTEGER NOT NULL,
    tipo                  VARCHAR(30) DEFAULT 'TRONCO'
);

-- AULA
CREATE TABLE aulas (
    id        BIGSERIAL PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    codigo    VARCHAR(20) NOT NULL UNIQUE,
    capacidad INTEGER,
    tipo      VARCHAR(30) DEFAULT 'COMUN'
);

-- CURSO (Asignación Docente-Aula por Grado/Materia)
CREATE TABLE cursos (
    id            BIGSERIAL PRIMARY KEY,
    periodo_id    BIGINT NOT NULL REFERENCES periodos_academicos(id),
    grado_id      BIGINT NOT NULL REFERENCES grados(id),
    seccion_id    BIGINT NOT NULL REFERENCES secciones(id),
    materia_id    BIGINT NOT NULL REFERENCES materias(id),
    docente_id    BIGINT REFERENCES docentes(id),
    aula_id       BIGINT REFERENCES aulas(id),
    estado        VARCHAR(30) DEFAULT 'BORRADOR',
    UNIQUE(periodo_id, grado_id, seccion_id, materia_id)
);

-- HORARIO BLOQUE
CREATE TABLE horario_bloque (
    id              BIGSERIAL PRIMARY KEY,
    curso_id        BIGINT NOT NULL REFERENCES cursos(id),
    dia_semana      INTEGER NOT NULL CHECK (dia_semana BETWEEN 1 AND 5),
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    aula_id         BIGINT REFERENCES aulas(id),
    UNIQUE(curso_id, dia_semana, hora_inicio)
);

-- BIMESTRE
CREATE TABLE bimestres (
    id          BIGSERIAL PRIMARY KEY,
    periodo_id  BIGINT NOT NULL REFERENCES periodos_academicos(id),
    numero      INTEGER NOT NULL CHECK (numero BETWEEN 1 AND 4),
    nombre      VARCHAR(50) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin   DATE NOT NULL,
    estado      VARCHAR(30) DEFAULT 'ABIERTO',
    UNIQUE(periodo_id, numero)
);

-- TIPO DE EVALUACIÓN
CREATE TABLE tipos_evaluacion (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    peso_porcentaje DECIMAL(5,2) NOT NULL,
    orden           INTEGER NOT NULL
);

-- EVALUACIÓN
CREATE TABLE evaluaciones (
    id                  BIGSERIAL PRIMARY KEY,
    curso_id            BIGINT NOT NULL REFERENCES cursos(id),
    bimestre_id         BIGINT NOT NULL REFERENCES bimestres(id),
    tipo_evaluacion_id  BIGINT NOT NULL REFERENCES tipos_evaluacion(id),
    nombre              VARCHAR(100) NOT NULL,
    fecha               DATE NOT NULL,
    ponderacion_override DECIMAL(5,2),
    UNIQUE(curso_id, bimestre_id, tipo_evaluacion_id, nombre)
);

-- NOTA
CREATE TABLE notas (
    id                BIGSERIAL PRIMARY KEY,
    evaluacion_id     BIGINT NOT NULL REFERENCES evaluaciones(id),
    alumno_id         BIGINT NOT NULL REFERENCES alumnos(id),
    valor             DECIMAL(4,2) NOT NULL CHECK (valor >= 0 AND valor <= 20),
    observacion       TEXT,
    fecha_registro    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    registrado_por_id BIGINT REFERENCES usuarios(id),
    UNIQUE(evaluacion_id, alumno_id)
);

-- ASISTENCIA - TIPO
CREATE TABLE tipos_asistencia (
    id                    BIGSERIAL PRIMARY KEY,
    codigo                VARCHAR(20) NOT NULL UNIQUE,
    nombre                VARCHAR(100) NOT NULL,
    computa_como_presente BOOLEAN DEFAULT TRUE
);

-- SESIÓN DE CLASE
CREATE TABLE sesiones_clase (
    id              BIGSERIAL PRIMARY KEY,
    curso_id        BIGINT NOT NULL REFERENCES cursos(id),
    horario_bloque  BIGINT REFERENCES horario_bloque(id),
    fecha           DATE NOT NULL,
    tema            VARCHAR(255),
    estado          VARCHAR(30) DEFAULT 'PROGRAMADA'
);

-- ASISTENCIA DEL ALUMNO
CREATE TABLE asistencias_alumno (
    id                  BIGSERIAL PRIMARY KEY,
    sesion_id           BIGINT NOT NULL REFERENCES sesiones_clase(id),
    alumno_id           BIGINT NOT NULL REFERENCES alumnos(id),
    tipo_asistencia_id  BIGINT NOT NULL REFERENCES tipos_asistencia(id),
    minutos_tardanza    INTEGER,
    observacion         TEXT,
    registrado_por_id   BIGINT REFERENCES usuarios(id),
    fecha_registro      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sesion_id, alumno_id)
);

-- ALERTA DE FALTAS CONSECUTIVAS
CREATE TABLE alertas_faltas (
    id                  BIGSERIAL PRIMARY KEY,
    alumno_id           BIGINT NOT NULL REFERENCES alumnos(id),
    curso_id            BIGINT NOT NULL REFERENCES cursos(id),
    cantidad_consecutivas INTEGER NOT NULL,
    nivel               VARCHAR(20) DEFAULT 'ALTA',
    estado              VARCHAR(20) DEFAULT 'NUEVA',
    fechas              JSONB,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resuelta_at         TIMESTAMP
);
```
