# SGE - TAREAS TÉCNICAS DE DESARROLLO POR CAPAS

**Versión:** 1.0  
**Fecha:** 2026-07-04  
**Metodología:** Trunk-based development, Commits convencionales (Conventional Commits), PRs obligatorios con revisión de arquitectura.  
**Definición de Hecho (DoD):** Código + Tests (Unit/Integration) + Documentación OpenAPI + Lint/Spotless OK + Migración Flyway + Despliegue en entorno `dev`.

---

## 1. BACKEND — SPRING BOOT 3.3+ (JAVA 21) + POSTGRESQL 16

### 1.1. INFRAESTRUCTURA Y CONFIGURACIÓN BASE

| ID | Tarea | Descripción Técnica / Criterios de Aceptación |
|----|-------|-----------------------------------------------|
| **BE-INFRA-01** | **Inicializar Proyecto Spring Boot** | `spring init --dependencies=web,data-jpa,postgresql,flyway,validation,security,actuator,openapi -d=org.sge:sge-backend -n=sge-backend --java-version=21 --build=maven` |
| **BE-INFRA-02** | **Configurar `application.yml` por perfiles** | Perfiles: `dev`, `test`, `prod`. Datasource HikariCP (pool 20). `spring.jpa.open-in-view=false`. `spring.flyway.enabled=true`. `springdoc.api-docs.path=/api-docs`. |
| **BE-INFRA-03** | **Configurar Flyway (Migraciones DB)** | `src/main/resources/db/migration`. Naming: `V{version}__{description}.sql`. Baseline on migrate `true`. Scripts: esquema `sge`, extensiones `uuid-ossp`, `pg_trgm`. |
| **BE-INFRA-04** | **Configurar Spring Security + JWT** | `SecurityFilterChain` stateless. `JwtAuthenticationFilter` (HS256/RS256, expiración, blacklist refresh). `DaoAuthenticationProvider` (BCrypt 12). `AuthenticationEntryPoint` (401), `AccessDeniedHandler` (403). |
| **BE-INFRA-05** | **Configurar OpenAPI / Swagger** | `OpenAPI` Bean: info, servers, security scheme `bearerAuth` (JWT), global responses. Tags por módulo. Generar `openapi.yaml` en fase `compile` (plugin `springdoc-openapi-maven-plugin`). |
| **BE-INFRA-06** | **Configurar Manejo Global de Excepciones** | `@RestControllerAdvice`: 400 (FieldErrors), 404, 409 (BusinessRuleViolation), 403, 500. Log JSON con `traceId` (MDC). |
| **BE-INFRA-07** | **Configurar Auditing (JPA + Custom)** | `AuditorAwareImpl` (userId de SecurityContext). Campos: `created_at/created_by/updated_at/updated_by`. Tabla `audit_log` (entity_type, entity_id, action, diff_json, user_id, timestamp). |
| **BE-INFRA-08** | **Configurar Testcontainers** | `@TestConfiguration` con `@Container PostgreSQLContainer`. Perfil `test` usa Testcontainers. Flyway maneja schema. |

---

### 1.2. MODELO DE DATOS — ENTIDADES JPA Y RELACIONES

> Convención: `Long id` (IDENTITY), `@Version` (Optimistic Lock), `Instant` timestamps UTC. `snake_case` en BD, `camelCase` en Java.

| ID | Entidad | Relaciones Clave | Detalle |
|----|---------|------------------|---------|
| **BE-JPA-01** | `PeriodoAcademico` | — | nombre, codigo UNIQUE, fecha_inicio, fecha_fin, estado (PLANIFICACION, ACTIVO, CERRADO, FINALIZADO) |
| **BE-JPA-02** | `Grado` | → List`<Seccion>` | nombre, nivel (INICIAL, PRIMARIA, SECUNDARIA), orden, capacidad_max |
| **BE-JPA-03** | `Seccion` | ← Grado (N:1) | nombre, capacidad |
| **BE-JPA-04** | `Materia` | — | nombre, codigo UNIQUE, horas_semanales_req, tipo (TRONCO, ELECTIVA, TALLER) |
| **BE-JPA-05** | `Aula` | — | nombre, codigo UNIQUE, capacidad, tipo (COMUN, LABORATORIO, TALLER, DEPORTIVO) |
| **BE-JPA-06** | `Usuario` | ↔ `Rol` (N:M via `UsuarioRol`) | username UNIQUE, email UNIQUE, password_hash, enabled, last_login |
| **BE-JPA-07** | `Rol` | ↔ `Permiso` (N:M via `RolPermiso`) | codigo (ADMIN, DOCENTE, ALUMNO, PADRE), nombre |
| **BE-JPA-08** | `Permiso` | ← `RolPermiso` | codigo (USUARIO_CREAR, NOTA_CREAR, ...), descripcion, modulo |
| **BE-JPA-09** | `Persona` (Joined) | → `Usuario` (1:1) | class abstract. dni UNIQUE, nombres, apellidos, fecha_nac, genero, telefono, direccion, foto_url |
| **BE-JPA-10** | `Docente` extends `Persona` | ← `Curso.docente` (1:N) | codigo_empleado UNIQUE, especialidad, tipo_contrato, carga_horaria_max |
| **BE-JPA-11** | `Alumno` extends `Persona` | → `EstadoAlumno` (N:1), ↔ `Padre` (N:M via `AlumnoPadre`) | codigo_estudiante UNIQUE, estado_actual_id, sub_estado, fecha_ingreso, fecha_ultimo_estado |
| **BE-JPA-12** | `Padre` extends `Persona` | ↔ `Alumno` (N:M via `AlumnoPadre`) | — |
| **BE-JPA-13** | `AlumnoPadre` | ← `Alumno` (N:1), ← `Padre` (N:1) | parentesco (PADRE, MADRE, APODERADO), es_titular. PK compuesta. |
| **BE-JPA-14** | `EstadoAlumno` | ← `Alumno` (1:N), ↔ `TransicionEstado` | codigo UNIQUE, nombre, es_terminal, es_transitorio, permiso_acceso |
| **BE-JPA-15** | `TransicionEstado` | → `EstadoAlumno` origen (N:1), → destino (N:1) | codigo_gatillante, es_automatica, requiere_admin, requiere_consejo, notifica_padre, UNIQUE(origen, destino, gatillante) |
| **BE-JPA-16** | `HistorialEstadoAlumno` | ← `Alumno` (N:1), ← `TransicionEstado`, → `EstadoAlumno` ant/nuevo | motivo, documento_url, fecha_cambio, vigencia_hasta |
| **BE-JPA-17** | `Curso` | → `Periodo` (N:1), → `Grado` (N:1), → `Seccion` (N:1), → `Materia` (N:1), → `Docente` (N:1), → `Aula` (N:1) | estado (BORRADOR, ASIGNADO, ACTIVO, FINALIZADO). UNIQUE(periodo, grado, seccion, materia) |
| **BE-JPA-18** | `HorarioBloque` | ← `Curso` (N:1), → `Aula` (N:1) | dia_semana (1-5), hora_inicio, hora_fin. UNIQUE(curso, dia, hora_inicio) |
| **BE-JPA-19** | `Bimestre` | → `Periodo` (N:1) | numero (1-4), nombre, fecha_inicio, fecha_fin, estado (ABIERTO, CERRADO, PUBLICADO) |
| **BE-JPA-20** | `TipoEvaluacion` | ← `Evaluacion` (1:N) | nombre, peso_porcentaje, orden |
| **BE-JPA-21** | `Evaluacion` | → `Curso` (N:1), → `Bimestre` (N:1), → `TipoEvaluacion` (N:1) | nombre, fecha, ponderacion_override. UNIQUE(curso, bimestre, tipo_evaluacion, nombre) |
| **BE-JPA-22** | `Nota` | → `Evaluacion` (N:1), → `Alumno` (N:1) | valor DECIMAL(4,2) CHECK 0-20, observacion, registrado_por. UNIQUE(evaluacion, alumno) |
| **BE-JPA-23** | `TipoAsistencia` | ← `AsistenciaAlumno` (1:N) | codigo UNIQUE (PRESENTE, FALTA, TARDANZA, JUSTIFICADO, LICENCIA), computa_como_presente |
| **BE-JPA-24** | `SesionClase` | → `Curso` (N:1), → `HorarioBloque` (N:1) | fecha, tema, estado (PROGRAMADA, EN_CURSO, CONFIRMADA, ANULADA) |
| **BE-JPA-25** | `AsistenciaAlumno` | → `SesionClase` (N:1), → `Alumno` (N:1), → `TipoAsistencia` (N:1) | minutos_tardanza, observacion. UNIQUE(sesion, alumno) |
| **BE-JPA-26** | `AlertaFaltasConsecutivas` | → `Alumno` (N:1), → `Curso` (N:1) | cantidad_consecutivas, nivel (ALTA, CRITICA), estado (NUEVA, VISTA, RESUELTA), fechas JSONB |
| **BE-JPA-27** | `ConceptoPago` | → `Grado` (N:1 nullable) | nombre, monto_base, periodicidad |
| **BE-JPA-28** | `CronogramaPago` | → `ConceptoPago` (N:1), → `Periodo` (N:1), → `Alumno` (N:1) | fecha_vencimiento, monto, estado (PENDIENTE, PAGADO, VENCIDO, ANULADO) |
| **BE-JPA-29** | `Pago` | → `CronogramaPago` (N:1), → `Alumno` (N:1) | monto, metodo, referencia, fecha_pago, usuario_id |
| **BE-JPA-30** | `Notificacion` | → `Usuario` (N:1) | titulo, cuerpo, tipo (INFO, ALERTA, CRITICA), leida, entidad_relacionada |
| **BE-JPA-31** | `Documento` | — | entidad_tipo, entidad_id, tipo_doc, nombre_archivo, url, mime_type, generado_por |

---

### 1.3. REPOSITORIOS (Spring Data JPA)

| ID | Repositorio | Métodos Custom Clave (`@Query`) |
|----|-------------|---------------------------------|
| **BE-REP-01** | `CursoRepository` | `findByPeriodoIdAndEstadoIn(Long, List)`, `findCargaHorariaDocente(Long docenteId, Long periodoId)` |
| **BE-REP-02** | `HorarioBloqueRepository` | **`findConflictos(Long excludeCursoId, Integer dia, LocalTime ini, LocalTime fin, Long docenteId, Long aulaId, Long gradoSeccionId)`** → SQL colisión (ver Reglas Negocio). |
| **BE-REP-03** | `AlumnoRepository` | `findByEstadoActualCodigo(String)`, `findByPadreId(Long)`, `countByEstadoActualCodigoAndPeriodo(String, Long)` |
| **BE-REP-04** | `NotaRepository` | `findByAlumnoIdAndBimestreId(Long, Long)`, `findPromedioPonderadoByAlumnoAndBimestre(Long, Long)` |
| **BE-REP-05** | `AsistenciaAlumnoRepository` | **`findFaltasConsecutivas(Long alumnoId, Long cursoId, LocalDate fechaRef, Integer minCons)`** → query ventana/ranking por fecha. |
| **BE-REP-06** | `TransicionEstadoRepository` | `findByEstadoOrigenIdAndEstadoDestinoId(Long, Long)`, `findAutomaticasByOrigen(Long)` |
| **BE-REP-07** | `HistorialEstadoAlumnoRepository` | `findByAlumnoIdOrderByFechaCambioDesc(Long)` |
| **BE-REP-08** | `AlertaFaltasRepository` | `findByEstadoOrderByCreatedAtDesc(String)`, `countByEstado(String)` |

---

### 1.4. SERVICIOS — LÓGICA DE NEGOCIO (`@Service @Transactional`)

| ID | Servicio | Métodos Clave y Reglas |
|----|----------|------------------------|
| **BE-SVC-01** | `PeriodoAcademicoService` | `activarPlanEstudios(Long periodoId)`: Valida cursos con docente+aula, horas ≤ límite grado. Cambia estado a ACTIVO. |
| **BE-SVC-02** | `HorarioService` (CRÍTICO) | `validarYAsignarBloque(HorarioBloqueDTO)`: Llama `HorarioBloqueRepository.findConflictos()` para docente, aula, grado/sección. Si hay colisión → `BusinessRuleViolationException("COLISION_HORARIO")`. `generarHorarioAutomatico(Long periodoId)`: CSP/Backtracking vía OptaPlanner o heurística. `@Async`. Devuelve `HorarioGeneradoResult`. |
| **BE-SVC-03** | `NotaService` | `registrarNotas(Long evaluacionId, List<NotaDTO>)`: Valida bimestre ABIERTO, rango 0-20, alumno ∈ curso. Bulk upsert. Recalcula promedios ponderados. `publicarNotas(Long cursoId, Long bimestreId)`: Cambia estado a PUBLICADO. Dispara `NotasPublicadasEvent`. |
| **BE-SVC-04** | `AsistenciaService` | `registrarAsistencia(Long sesionId, List<AsistenciaDTO>)`: Upsert. `confirmarSesion(Long sesionId)` → Cambia estado a CONFIRMADA. **Dispara `AsistenciaConfirmadaEvent`** (async). |
| **BE-SVC-05** | `AsistenciaAlertListener` (CRÍTICO) | `@EventListener` escucha `AsistenciaConfirmadaEvent`. Por cada alumno con FALTA: llama `findFaltasConsecutivas()`. Si ≥3 → Crea `AlertaFaltas`. Si ≥5 → nivel CRITICA + escalamiento a Director. |
| **BE-SVC-06** | `EstadoAlumnoService` | `transicionar(Long alumnoId, String nuevoEstado, TransicionRequest)`: Valida transición permitida, roles (ADMIN, Consejo), ejecuta reglas específicas (bloqueo usuario SUSPENDIDO/BAJA, generar certificado EGRESADO), persiste historial, dispara `EstadoCambiadoEvent`. |
| **BE-SVC-07** | `AlumnoService` | `importarDesdeExcel(Long periodoId, MultipartFile)`: Apache POI. Valida headers. Chunk 100. Genera código AÑO-SEC. Crea Usuario. Devuelve `ImportResult`. |
| **BE-SVC-08** | `ReporteService` | `generarLibretaAlumno(Long alumnoId, Long bimestreId, ExportOptions)`: JasperReports/OpenPDF. QR, firma digital. `generarRendimientoGrado(Long gradoId, Long periodoId)`: Dataset para gráficos. Exporta PDF/Excel. |

---

### 1.5. CONTROLADORES REST (`@RestController`, `@RequestMapping("/api/v1")`)

> **Seguridad:** `@PreAuthorize("hasAuthority('PERMISO_CODIGO')")` o `@PreAuthorize("hasRole('ADMIN')")`.  
> **OpenAPI:** `@Operation`, `@ApiResponses`, `@Schema` en DTOs.

| ID | Controlador | Endpoints |
|----|-------------|-----------|
| **BE-CTL-01** | `PeriodoAcademicoController` | `GET /periodos`, `POST /periodos` (ADMIN), `PUT /periodos/{id}/activar-plan` (ADMIN), `GET /periodos/activo` (ALL) |
| **BE-CTL-02** | `CursoController` | `GET /cursos?periodoId=` (ADMIN, DOCENTE), `POST /cursos` (ADMIN), `PUT /cursos/{id}/asignar-docente-aula` (ADMIN), `GET /cursos/{id}/horario` (ADMIN, DOCENTE) |
| **BE-CTL-03** | `HorarioController` | `POST /horario/bloques` (ADMIN), `POST /horario/generar-automatico?periodoId=` (ADMIN @Async), `GET /horario/jobs/{jobId}`, `GET /horario/semanal?vista=grado\|docente\|aula&id=` (ADMIN, DOCENTE), `POST /horario/bloques/{id}/mover` (ADMIN drag&drop), `GET /horario/exportar?vista=&id=&formato=PDF\|EXCEL` |
| **BE-CTL-04** | `NotaController` | `GET /cursos/{cursoId}/bimestres/{bimestreId}/notas` (DOCENTE), `PUT /cursos/{cursoId}/bimestres/{bimestreId}/notas` (DOCENTE), `POST /cursos/{cursoId}/bimestres/{bimestreId}/notas/importar` (DOCENTE Excel), `POST /cursos/{cursoId}/bimestres/{bimestreId}/publicar` (ADMIN), `GET /alumnos/{alumnoId}/periodos/{periodoId}/reporte-notas` (ALUMNO, PADRE), `GET /alumnos/{alumnoId}/cursos/{cursoId}/tendencia` |
| **BE-CTL-05** | `AsistenciaController` | `GET /cursos/{cursoId}/sesiones?fecha=` (DOCENTE), `GET /sesiones/{sesionId}/asistencia` (DOCENTE), `PUT /sesiones/{sesionId}/asistencia` (DOCENTE), `POST /sesiones/{sesionId}/confirmar` (DOCENTE), `PATCH /sesiones/{sesionId}/asistencia/{alumnoId}` (corrección 48h), `GET /alertas/faltas-consecutivas?cursoId=` (ADMIN, DOCENTE) |
| **BE-CTL-06** | `EstadoAlumnoController` | `GET /estados-alumno` (ADMIN), `GET /estados-alumno/transiciones` (ADMIN), `GET /alumnos/{alumnoId}/historial-estados` (ADMIN), `POST /alumnos/{alumnoId}/transicion-estado` (ADMIN) |
| **BE-CTL-07** | `AlumnoController` | `POST /periodos/{periodoId}/alumnos/importar` (ADMIN Excel), `POST /alumnos/{alumnoId}/padres` (ADMIN asociar), `POST /alumnos/{alumnoId}/padres/nuevo` (ADMIN crear+asociar) |
| **BE-CTL-08** | `DocenteController` | `GET /docentes` (ADMIN), `POST /docentes` (ADMIN), `GET /docentes/{id}/carga-horaria?periodoId=` |
| **BE-CTL-09** | `ReporteController` | `GET /reportes/libreta/alumno/{id}/bimestre/{id}` (ADMIN, PADRE), `GET /reportes/libreta/curso/{id}/bimestre/{id}` (ADMIN, DOCENTE), `GET /reportes/rendimiento/grado/{id}/periodo/{id}` (ADMIN) |
| **BE-CTL-10** | `AuthController` | `POST /auth/login` (PUBLIC), `POST /auth/refresh` (PUBLIC), `POST /auth/logout` (AUTH), `GET /auth/me` (AUTH) |

---

### 1.6. GENERACIÓN DE `openapi.yaml` (TAREA OBLIGATORIA)

| ID | Tarea | Comando / Configuración |
|----|-------|--------------------------|
| **BE-OAS-01** | **Añadir Plugin Maven `springdoc-openapi-maven-plugin`** | `pom.xml`: goal `generate` en fase `compile`. Config: `outputFile=openapi.yaml`, `outputDir=${project.build.directory}/openapi`. |
| **BE-OAS-02** | **Generar Archivo en Build** | `mvn clean compile` → verificar `target/openapi/openapi.yaml` existe y es válido (OpenAPI 3.1.0). |
| **BE-OAS-03** | **Validar Contrato en CI** | Job: `mvn validate` + `swagger-codegen validate -i target/openapi/openapi.yaml`. Publicar artefacto para Frontend. |
| **BE-OAS-04** | **Documentar DTOs con `@Schema`** | Todos los DTOs Request/Response deben tener `@Schema(description="...", example="...")`. Enums con `@Schema(enumAsRef=true)`. |

---

## 2. GENERACIÓN AUTOMÁTICA DE CÓDIGO FRONTEND (FLUJO CLI)

> **Objetivo:** Sincronización **Single Source of Truth** (Backend OpenAPI → Frontend TypeScript).  
> Ejecutar **cada vez que `openapi.yaml` cambie** (Pipeline CI/CD o local manual).

| ID | Tarea | Comando / Detalle |
|----|-------|-------------------|
| **GEN-01** | **Instalar Generador Global** | `npm i -g @openapitools/openapi-generator-cli@latest` (Requiere Node 20+ y Java 21+ en PATH). |
| **GEN-02** | **Ejecutar Generación (TypeScript-Angular)** | ```bash<br>openapi-generator-cli generate \<br>  -i ./openapi.yaml \<br>  -g typescript-angular \<br>  -o src/app/core/models/dto \<br>  --additional-properties=\<br>    ngVersion=18,\<br>    supportsES6=true,\<br>    withInterfaces=true,\<br>    useSingleRequestParameter=false,\<br>    enumPropertyNaming=original,\<br>    modelPropertyNaming=camelCase,\<br>    paramNaming=camelCase,\<br>    strictNullChecks=true,\<br>    fileNaming=kebab-case,\<br>    serviceSuffix=Service,\<br>    modelFolder=models,\<br>    apiFolder=services\<br>``` |
| **GEN-03** | **Plantillas Personalizadas (Opcional)** | Copiar templates `typescript-angular` a `openapi-templates/custom/`. Modificar `service.mustache` para inyectar `ApiClientService` wrapper. |
| **GEN-04** | **Post-Generación: Lint y Formato** | `npm run lint -- --fix` + `prettier --write` sobre `src/app/core/models/dto`. `git add src/app/core/models/dto`. |
| **GEN-05** | **Integrar en CI/CD** | Pipeline: `backend-build` → publica `openapi.yaml` (artefacto) → `frontend-generate` (descarga, ejecuta GEN-02, PR automático o commit si hay diff). |

---

## 3. FRONTEND — ANGULAR 18+ (STANDALONE, SIGNALS, ZONELESS) + SCSS

### 3.1. INICIALIZACIÓN Y CONFIGURACIÓN BASE

| ID | Tarea | Comando / Configuración |
|----|-------|--------------------------|
| **FE-INIT-01** | **Crear Workspace Angular** | `ng new sge-frontend --standalone --ssr=false --style=scss --routing --prefix=sge --package-manager=npm --strict` |
| **FE-INIT-02** | **Configurar `angular.json`** | `outputPath: dist/sge-frontend`. `styles: ["src/styles.scss"]`. `budgets: initial < 500kb, component < 50kb`. |
| **FE-INIT-03** | **Configurar `tsconfig.json` (Strict)** | `strict: true`, `noUncheckedIndexedAccess`, `paths: { "@core/*": ["src/app/core/*"], "@shared/*": ..., "@features/*": ..., "@env/*": ... }`. |
| **FE-INIT-04** | **Instalar Dependencias Core** | `npm i @angular/material @angular/cdk @angular/animations chart.js ngx-charts @ngrx/signals @fortawesome/fontawesome-free ngx-toastr ngx-spinner dayjs` |
| **FE-INIT-05** | **Instalar Dev Dependencies** | `npm i -D @angular-eslint/schematics eslint prettier husky lint-staged @commitlint/cli @commitlint/config-conventional` |
| **FE-INIT-06** | **Configurar Husky + Lint-Staged** | `npx husky install`. Pre-commit hook: lint-staged sobre `*.{ts,html,scss}` (eslint --fix + prettier --write). |
| **FE-INIT-07** | **Configurar Environments** | `src/environments/environment.ts` (dev): `apiBaseUrl: 'http://localhost:8080/api/v1'`, `authTokenKey: 'sge_token'`. `environment.prod.ts`: variables producción. |
| **FE-INIT-08** | **Ejecutar Generación OpenAPI (GEN-02)** | Copiar `openapi.yaml` → raíz frontend. Ejecutar comando GEN-02. Verificar `src/app/core/models/dto` poblado con DTOs y servicios. |

---

### 3.2. ARQUITECTURA LIMPIA — ESTRUCTURA DE CARPETAS

```
src/app/
├── core/                           # Singleton Services, Guards, Interceptors, Models, Store
│   ├── auth/
│   │   ├── auth.service.ts         # Signal<currentUser>, login(), refresh(), logout(), hasRole(), hasPermission()
│   │   ├── jwt.interceptor.ts      # Adjunta Access Token, maneja 401 → Refresh → Retry
│   │   ├── auth.guard.ts           # canActivate: isAuthenticated()
│   │   └── role.guard.ts           # canActivate: hasRole(['ADMIN']) / hasPermission(['NOTA_CREAR'])
│   ├── http/
│   │   ├── api-client.service.ts   # Wrapper HttpClient genérico, baseUrl, error mapping, loading global
│   │   └── loading.interceptor.ts  # NgxSpinner show/hide automático
│   ├── state/
│   │   ├── app-store.service.ts    # SignalStore: { user, periodoActivo, alertas[], sidebarOpen, theme }
│   │   └── notification.service.ts # Wrapper ngx-toastr (success, error, warning, info)
│   ├── models/
│   │   ├── dto/                    # GENERADO AUTOMÁTICAMENTE (GEN-02) - NO TOCAR MANUALMENTE
│   │   └── enums/                  # Enums locales UI (EstadoAlumnoView, TipoAsistenciaView, Severity)
│   └── utils/
│       ├── date.util.ts            # formatDate, getBimestreActual, diasHabiles
│       └── validation.util.ts      # Validators: rangoNotas(0,20), dniPeruano
│
├── shared/                         # Dumb Components, Pipes, Directives (Design System)
│   ├── components/
│   │   ├── ui/                     # Átomos / Moléculas
│   │   │   ├── btn/                # SgeButtonComponent (variant, size, loading, icon)
│   │   │   ├── card/               # SgeCardComponent (header, actions, loading)
│   │   │   ├── table/              # SgeTableComponent<T> (sort, paginación, inline-edit, row-select, export)
│   │   │   ├── grid/               # SgeScheduleGridComponent (Ver 3.3.1)
│   │   │   ├── form/               # SgeInput, SgeSelect, SgeDatepicker, SgeFileUpload
│   │   │   ├── alert/              # SgeAlertCardComponent (type: info|warning|danger|success, actions)
│   │   │   ├── badge/              # SgeEstadoBadgeComponent (codigoEstado → label + color + icon)
│   │   │   ├── chart/              # SgeChartWrapperComponent (type: bar|line|doughnut|radar)
│   │   │   └── modal/              # SgeConfirmModal, SgeFormModal (size, draggable)
│   │   ├── layout/
│   │   │   ├── sidebar/            # SgeSidebarComponent (menu recursivo por rol, collapsible)
│   │   │   ├── header/             # SgeHeaderComponent (user menu, notificaciones, periodo selector)
│   │   │   └── breadcrumb/         # SgeBreadcrumbComponent
│   │   └── feedback/
│   │       ├── loading-overlay/    # SgeLoadingOverlayComponent
│   │       └── empty-state/        # SgeEmptyStateComponent (icon, title, description, action)
│   ├── pipes/
│   │   ├── estado-label.pipe.ts    # 'ACTIVO' → 'Activo'
│   │   ├── nota-color.pipe.ts      # 15.5 → 'text-success', 10.2 → 'text-danger'
│   │   └── asistencia-label.pipe.ts
│   ├── directives/
│   │   ├── permissions.directive.ts # *sgeHasPermission="['NOTA_CREAR']"
│   │   └── focus-trap.directive.ts
│   └── shared.module.ts            # Exporta CommonModule, MaterialModules, FormsModule, ReactiveFormsModule
│
├── features/                       # Smart Components / Pages - Lazy Loaded por Dominio/Rol
│   ├── auth/
│   │   ├── login/                  # Reactive Form, rememberMe, forgot password
│   │   └── auth.routes.ts
│   ├── admin/                      # canActivate: [AuthGuard, RoleGuard(['ADMIN'])]
│   │   ├── dashboard/              # HU-16: KPIs, Alertas, Gráficos
│   │   ├── plan-estudios/          # HU-01: CRUD Grados/Materias/Horas
│   │   ├── asignacion-docente-aula/ # HU-02: Matriz conflictos
│   │   ├── generador-horario/      # HU-03: ScheduleGrid + Drag-Drop + Auto
│   │   ├── alumnos/                # HU-04, HU-06: Importar Excel, Asociar Padres
│   │   ├── docentes/               # HU-05: CRUD Docente
│   │   ├── estados-alumno/         # Máquina de Estados: Grafo Visual, Transición
│   │   ├── reportes/               # HU-13,14,15: Exportar PDF/Excel
│   │   └── admin.routes.ts
│   ├── docente/                    # canActivate: [AuthGuard, RoleGuard(['DOCENTE'])]
│   │   ├── dashboard/              # Mis Cursos, Alertas Faltas
│   │   ├── notas/                  # HU-07, HU-09: InlineEditTable + Importar Excel
│   │   ├── asistencia/             # HU-10: Lista Checkboxes/Chips + Confirmar
│   │   ├── horario/                # HU-14: ScheduleGrid ReadOnly + Export
│   │   └── docente.routes.ts
│   ├── alumno/                     # canActivate: [AuthGuard, RoleGuard(['ALUMNO']), EstadoActivoGuard]
│   │   ├── dashboard/              # Mi Horario, Mis Notas, Mis Alertas
│   │   ├── notas/                  # HU-08: Tabla Bimestres + Gráfico Tendencia
│   │   ├── asistencia/             # Mi Asistencia Calendario + %
│   │   └── alumno.routes.ts
│   ├── padre/                      # canActivate: [AuthGuard, RoleGuard(['PADRE'])]
│   │   ├── dashboard/              # Selector Hijos + Resumen Alertas/Pagos
│   │   ├── hijos/                  # Notas (HU-08), Asistencia, Boletines
│   │   ├── pagos/                  # Estado Cuenta, Cronograma, Pago Online
│   │   ├── matricula/              # Solicitud/Renovación/Retiro
│   │   └── padre.routes.ts
│   └── shared-feature/             # Componentes cross-feature (AlumnoSelector, CursoSelector)
│
├── app.routes.ts                   # Rutas raíz + redirect según rol tras login
├── app.config.ts                   # provideHttpClient(withInterceptors), provideRouter, provideStore
└── main.ts                         # bootstrapApplication(AppComponent) + zoneless config
```

---

### 3.3. COMPONENTES COMPLEJOS — ESPECIFICACIÓN TÉCNICA

#### 3.3.1. `SgeScheduleGridComponent` (Horario Interactivo — HU-03, HU-14)

| Aspecto | Especificación |
|---------|----------------|
| **Inputs** | `horario: HorarioSemanalDTO`, `vista: 'grado' \| 'docente' \| 'aula'`, `modo: 'view' \| 'edit'`, `conflictos: ConflictoDTO[]` |
| **Outputs** | `bloqueMoved: EventEmitter<{bloque, nuevoDia, nuevaHora}>`, `conflictoDetectado: EventEmitter<ConflictoDTO[]>` |
| **Drag & Drop** | `@angular/cdk/drag-drop`. `cdkDropListConnectedTo` dinámico. Validación en drop → emite evento → padre llama API → si 409, revert + toast error. |
| **Virtual Scroll** | NO en grid (40 celdas fijas). SÍ en panel lateral de bloques disponibles (cdk-virtual-scroll-viewport). |
| **Renderizado** | `*ngFor` días (Lun-Vie) × horas (8am-3pm). `SgeScheduleCellComponent` por celda: materia-color bg, tooltip detalle. |
| **Estados** | `.celda-libre`, `.celda-ocupada`, `.celda-conflicto` (borde rojo 2px + `animation: pulse-border 1.5s`), `.celda-drag-over` (bg primary-100). |
| **Exportación** | Botón en toolbar padre → `HorarioService.exportar(vista, id, formato)` → `FileSaver.saveAs(blob, filename)`. |

#### 3.3.2. `SgeInlineEditTableComponent` (Notas / Asistencia — HU-07, HU-10)

| Aspecto | Especificación |
|---------|----------------|
| **Genérico** | `<T extends { id: string \| number }>` |
| **Inputs** | `columns: SgeColumnDef<T>[]`, `data: T[]`, `loading: boolean` |
| **ColumnDef** | `{ field, header, type: 'text' \| 'number' \| 'select' \| 'asistencia' \| 'badge', editable, validation[], options?, cellTemplate? }` |
| **Tipos Celda** | `number` (stepper 0-20, step 0.5, Enter/Tab navega). `asistencia` (chips P/F/T/J/L + popover minutos tardanza). `select` (dropdown searchable). |
| **Auto-Save** | `valueChanges.pipe(debounceTime(800), distinctUntilChanged(), switchMap(saveRow))`. Indicador "Guardando..." / "✔ Guardado" / "✗ Error" en fila. |
| **Toolbar** | Search, Filtros, Export, Bulk Actions ("Publicar Todas", "Marcar Todos Presentes"). |

---

### 3.4. TAREAS DE DESARROLLO FRONTEND (SPRINTS)

| Sprint | Enfoque | Tareas Clave |
|--------|---------|--------------|
| **Sprint 0** | **Setup & Core** | FE-INIT-01 a 08, GEN-01 a 05. Core: AuthService, JwtInterceptor, AuthGuard, RoleGuard, ApiClientService, AppStore, NotificationService. Shared: UI Kit (Btn, Card, Input, Badge, Alert, Modal). Layout: Sidebar, Header, Routing Shell. |
| **Sprint 1** | **Auth & Admin Core** | Login (FE-AUTH). Admin Dashboard (KPIs, Alertas). PlanEstudios CRUD. Docentes CRUD. |
| **Sprint 2** | **Horarios** | ScheduleGrid (ver 3.3.1). Asignación Docente-Aula. Generador Automático (Job Async). Drag-Drop + Persistencia. |
| **Sprint 3** | **Alumnos y Estados** | Importar Excel (Wizard 3 pasos). Asociar Padres. Máquina de Estados Admin (Grafo Visual + Formulario Transición). |
| **Sprint 4** | **Notas (Docente)** | InlineEditTable (ver 3.3.2). Matriz Notas. Cálculo Promedio. Importar Excel. Publicar. |
| **Sprint 5** | **Asistencia + Alertas** | Sesiones Diarias. Registro Rápido (Default Presente + Chips). Confirmar. Widget Alertas Faltas Consecutivas. |
| **Sprint 6** | **Portales Alumno/Padre** | Alumno Dashboard + Notas/Gráfico Tendencia. Padre Selector Hijos + Notas + Asistencia + Boletines + Pagos. |
| **Sprint 7** | **Reportes** | Libreta PDF (Jasper/OldPDF). Horario PDF/Excel. Rendimiento Grado PDF con gráficos. |
| **Sprint 8** | **Pulido, Tests, CI/CD** | WCAG 2.1 AA, Cypress E2E, Jest Unit, Storybook, Bundle Analyzer, Docker + K8s. |

---

### 3.5. CHECKLIST DE CALIDAD FRONTEND (Definition of Done)

- [ ] **TypeScript Strict:** Sin `any`. Interfaces de OpenAPI usadas directamente.
- [ ] **Signals:** Estado local con `signal()/computed()/effect()`. Sin BehaviorSubject salvo Servicios Core.
- [ ] **OnPush:** `ChangeDetectionStrategy.OnPush` en todos los componentes.
- [ ] **Accesibilidad:** Semántica HTML5, Contraste AA, Navegación Teclado, ARIA roles en grid/tablist/dialog.
- [ ] **SCSS:** Uso exclusivo de variables (`$color-primary-600`), mixins (`@include respond-to(md)`). Sin valores hardcodeados.
- [ ] **Tests:** Unit ≥80% (Services/Pipes/Utils), Component (TestingLibrary), E2E (Cypress Happy Path).
- [ ] **Performance:** `trackBy` en `*ngFor`, virtual scroll en listas >50, lazy loading confirmado en Network, bundle <100kb gzipped por feature.
- [ ] **Documentación:** Storybook para Shared Components. README en cada Feature Module.
