# SGE - GUÍA DE DISEÑO FIGMA Y ARQUITECTURA UI/UX

**Versión:** 1.0  
**Fecha:** 2026-07-04  
**Herramienta:** Figma (Design System publicado como Library)  
**Metodología:** Atomic Design (Brad Frost) + Design Tokens (W3C DTCG Format)  
**Handoff:** Figma DevMode + Variables CSS/SCSS sincronizadas via Tokens Studio o Style Dictionary  

---

## 1. DISEÑO EN FIGMA — DESIGN SYSTEM (ATOMIC DESIGN)

### 1.1. ESTRUCTURA DE ARCHIVOS FIGMA (ORGANIZACIÓN DE PÁGINAS)

```
📁 SGE Design System (Archivo Principal - Library)
├── 📄 00 Foundations / Tokens
│   ├── Color Primitives & Semantic
│   ├── Typography Scale
│   ├── Spacing & Sizing
│   ├── Border Radius & Shadows
│   ├── Z-Index & Breakpoints
│   └── Motion & Easing
├── 📄 01 Atoms (Átomos)
│   ├── Button / Icon / Avatar / Badge / Tag
│   ├── Input / Select / Checkbox / Radio / Switch
│   ├── Tooltip / Toast / Spinner / Skeleton
│   └── Typography Components (Heading, Body, Label, Code)
├── 📄 02 Molecules (Moléculas)
│   ├── Form Field (Label + Input + Hint + Error)
│   ├── Search Input / Combobox / Date Picker / File Upload
│   ├── Table Row / Table Header / Pagination Controls
│   ├── Schedule Cell / Schedule Legend Item
│   ├── Alert Banner / Notification Toast / Confirm Dialog
│   ├── KPI Card / Stat Card / Progress Ring
│   ├── User Menu Item / Breadcrumb Item / Tab Item
│   └── Chart Tooltip / Legend Item
├── 📄 03 Organisms (Organismos)
│   ├── Header / Top Bar (Logo, Periodo, Notificaciones, User Menu)
│   ├── Sidebar Navigation (Collapsible, Role-based, Badges)
│   ├── Data Table (Toolbar, Virtual Scroll, Inline Edit, Bulk Actions)
│   ├── Schedule Grid (Week View, Drag-Drop, Conflict Overlay, Legend)
│   ├── Dashboard Widgets (KPI Grid, Alert List, Chart Container, Quick Actions)
│   ├── Wizard Steps (Import, Matricula, Configuracion)
│   ├── Modal Forms (Full, Medium, Small, Draggable)
│   ├── Alumno Detail Tabs (Datos, Notas, Asistencia, Horario, Docs, Historial)
│   └── Print Layouts (Libreta, Horario, Constancia - A4)
├── 📄 04 Templates (Plantillas / Page Layouts)
│   ├── Admin Layout (Sidebar + Header + Main Content + Footer)
│   ├── Docente Layout (Sidebar Compact + Header + Main)
│   ├── Alumno Layout (Header Simple + Tabs + Content)
│   ├── Padre Layout (Header + Hijo Selector Pills + Tabs)
│   ├── Login Layout (Centered Card, Brand, SSO)
│   └── Print Layout (A4, Margins, Header/Footer Repeating)
├── 📄 05 Pages / Flows (Pantallas Reales / User Flows)
│   ├── Admin: Dashboard, Plan Estudios, Horarios, Alumnos, Estados, Reportes
│   ├── Docente: Dashboard, Notas (Inline Edit), Asistencia, Horario, Plan Clase
│   ├── Alumno: Dashboard, Notas (Tendencia), Asistencia, Horario
│   ├── Padre: Dashboard, Hijo Detalle, Pagos, Matricula
│   └── Auth: Login, Recuperar Pass, Cambiar Pass, Error 403/404/500
└── 📄 06 Components States & Accessibility (Estados y Accesibilidad)
    ├── Focus Visible / Hover / Active / Disabled / Loading / Error / Skeleton
    ├── Dark Mode Variants (Todos los componentes)
    ├── Responsive Breakpoints (Mobile <768, Tablet 768-1024, Desktop >1024)
    └── WCAG 2.1 AA Checklist (Contraste, Focus Order, ARIA Labels)
```

---

### 1.2. ÁTOMOS (Átomos) — Bloques Básicos Indivisibles

| Componente | Propiedades / Variantes (Figma Variants) | Descripción Uso SGE |
|------------|------------------------------------------|---------------------|
| **Button** | **Variant:** `primary`, `secondary`, `outline`, `ghost`, `danger`, `success`<br>**Size:** `sm` (32px), `md` (40px), `lg` (48px), `icon-only` (40x40)<br>**State:** `default`, `hover`, `active`, `focus-visible`, `disabled`, `loading` (spinner interno)<br>**Icon:** `leading`, `trailing`, `none` | Acción principal (Guardar, Publicar, Confirmar). `danger` para Eliminar, Expulsar. `ghost` para acciones secundarias en tablas. |
| **Icon** | **Set:** FontAwesome 6 (Duotone/Regular/Solid) + Custom SVG (Escudo Colegio, QR)<br>**Size:** `xs` (12), `sm` (16), `md` (20), `lg` (24), `xl` (32)<br>**Color:** `currentColor` (hereda texto) o `semantic` (danger, success, warning) | Botones, Badges, Alertas, Navegación, Estados vacíos. **Nunca** usar imágenes raster para iconos UI. |
| **Avatar** | **Size:** `xs` (24), `sm` (32), `md` (40), `lg` (56), `xl` (80)<br>**Variant:** `image` (foto), `initials` (2-3 letras, bg generado por hash nombre), `placeholder` (icono usuario)<br>**Status Ring:** `none`, `online` (verde), `offline` (gris), `busy` (rojo), `away` (ámbar) - `position: bottom-right` | Header (usuario logueado), Filas de tabla (Alumno/Docente), Selector de hijos (Padre), Asignación docente. |
| **Badge / Tag** | **Variant:** `default` (neutral), `success` (verde), `warning` (ámbar), `danger` (rojo), `info` (azul), `primary` (azul marino)<br>**Size:** `sm` (18px h), `md` (22px h)<br>**Modifier:** `dot` (indicador 8px), `removable` (icono X), `clickable` (hover bg) | **Crítico:** `EstadoAlumnoBadge` (mapeo fijo: ACTIVO=success, SUSPENDIDO=danger, PRE_MATRICULADO=info, EGRESADO=primary, RETIRADO=default, BAJA=danger, ARCHIVADO=default). `AlertaFaltasBadge` (3 faltas=warning, 5+=danger). |
| **Input / Textarea** | **Size:** `sm`, `md`, `lg`<br>**State:** `default`, `hover`, `focus`, `error`, `disabled`, `read-only`<br>**Adornment:** `prefix` (icono/search), `suffix` (toggle password, clear, unit), `character-count`<br>**Type:** `text`, `email`, `password`, `number`, `tel`, `date`, `search` | Formularios universales. `number` con stepper para Notas (0-20, step 0.5). `date` con Datepicker nativo. |
| **Select / Combobox** | **Variant:** `native` (select nativo), `custom` (combobox searchable), `multi` (chips seleccionados)<br>**State:** Igual Input<br>**Options:** `grouped`, `async` (load more), `creatable` (nuevo tag) | Selección Grado/Sección/Materia, Docente, Aula, Bimestre, Tipo Evaluación. `async` para listas >50 (Alumnos, Padres). |
| **Checkbox / Radio / Switch** | **Size:** `sm` (16), `md` (20)<br>**State:** `unchecked`, `checked`, `indeterminate` (solo checkbox), `disabled`<br>**Label Position:** `right` (default), `left` | Checkbox: Selección múltiple tabla (Bulk Actions), "Todos Presentes". Radio: Opciones mutuamente excluyentes (Tipo Asistencia). Switch: Configuraciones ON/OFF (Notificaciones, Tema). |
| **Tooltip** | **Placement:** `top`, `bottom`, `left`, `right` (auto-flip)<br>**Trigger:** `hover`, `focus`, `click`<br>**Delay:** `show: 200ms`, `hide: 100ms`<br>**Variant:** `default` (dark bg), `light` (white bg + shadow) | Iconos de ayuda (?) en formularios, Celdas de horario (detalle completo), Badges truncados, Acciones de tabla. |
| **Spinner / Skeleton** | **Spinner Size:** `sm` (16), `md` (24), `lg` (32)<br>**Skeleton Variant:** `text` (lines), `circular` (avatar), `rectangular` (card, table row, chart), `table-row` (fila completa con celdas) | Loading global (Interceptor), Loading fila tabla (inline-edit), Loading grid horario, Placeholders tarjetas dashboard. |
| **Divider** | **Orientation:** `horizontal`, `vertical`<br>**Variant:** `solid`, `dashed`, `dotted` | Separación secciones Cards, Toolbar vs Tabla, Sidebar sections. |

---

### 1.3. MOLÉCULAS (Moléculas) — Combinaciones Funcionales Simples

| Componente | Composición (Átomos) | Comportamiento / Reglas SGE |
|------------|----------------------|------------------------------|
| **FormField** | `Label` + `Input/Select/Textarea` + `HintText` (opcional) + `ErrorMessage` (condicional) + `RequiredAsterisk` | **Unidad base de todos los formularios.** Maneja `id`/`htmlFor` accesibilidad. `ErrorMessage` solo visible si `touched && invalid`. `HintText` siempre visible (color neutral-500). |
| **SearchInput** | `Input` (type=search, prefix=Icon(lupa), suffix=Icon(X) si valor) + `Debounce` (300ms) | Header (búsqueda global), Filtros de tabla, Selector async (alumnos, docentes). |
| **DatePicker** | `Input` (readonly, suffix=Icon(calendario)) + `Popover` + `CalendarGrid` (Dayjs) + `Shortcuts` (Hoy, Inicio Mes, Fin Mes) | Filtros de rango (Reportes), Fecha evaluación, Fecha sesión clase. Rango: `from` / `to` (dos inputs sincronizados). |
| **FileUpload** | `Dropzone` (border dashed, hover bg primary-50) + `Input[type=file]` (hidden) + `FileList` (chips removibles) + `ProgressBar` + `ValidationErrors` | Importar Alumnos (Excel/CSV), Importar Notas (Excel), Subir Documentos (PDF/Img). Validar `accept`, `maxSize` (5MB), `maxFiles`. |
| **TableRow** | `Cells` (Tipados: Text, Number, Badge, Actions, Checkbox) + `Hover` + `Selected` + `Expanded` (detail row) + `Editing` (Inline) | **Base de `SgeTable`.** `Editing` muestra `FormField` por celda editable. `Expanded` muestra detalle (ej. evaluaciones de una nota). |
| **TableHeader** | `Columns` (Sortable: Icon flecha, Click handler) + `Filterable` (Icon filtro, Popover con Input/Select) + `Resizable` (CDK Drag horizontal) + `CheckboxAll` (Selección múltiple) | Toolbar integrada: Búsqueda global, Exportar, Acciones Bulk, Column Visibility. |
| **PaginationControls** | `PageSizeSelect` (10, 25, 50, 100) + `PageInfo` (Mostrando X-Y de Z) + `PageButtons` (First, Prev, Numbers, Next, Last) + `JumpToInput` | Server-side pagination obligatoria. `PageSize` persiste en LocalStorage por usuario. |
| **ScheduleCell** | `Container` (Grid area) + `BloqueHorario[]` (Máx 2 apilados, 3º = "+N más") + `DragHandle` (modo edit) + `ConflictBorder` (Rojo pulsante) + `Tooltip` (Detalle: Materia, Docente, Aula, Curso) | **Corazón del `ScheduleGrid`.** Click en `view` → Modal detalle. Drag en `edit` → `CdkDrag`. Color fondo = `materiaColor` (hash determinístico). |
| **ScheduleLegend** | `Chips` (Materia + Color + Código) + `ScrollX` (Auto) + `Filter` (Checkbox por materia) | Filtra visualmente celdas en Grid (opacity 0.2 las no seleccionadas). |
| **AlertBanner** | `Icon` (Semantic) + `Content` (Title + Description) + `Actions` (Buttons/Links) + `DismissButton` (X) + `BannerVariant` (Info/Warning/Danger/Success) | **Notificaciones persistentes en página** (ej. "Bimestre cerrado", "Horario generado con conflictos"). Distinto a Toast (transitorio). |
| **NotificationToast** | `Icon` + `Message` + `ActionButton` (Op) + `ProgressBar` (Auto-dismiss 5s) + `Stack` (Top-Right, Max 3) | `NotificationService` (success, error, warning, info). Accesible: `role="alert"`, `aria-live="polite"`. |
| **ConfirmDialog** | `Modal` (Size sm) + `Icon` (Warning/Question) + `Title` + `Message` + `CancelButton` (Ghost) + `ConfirmButton` (Danger/Primary) + `LoadingState` | Eliminar alumno, Publicar notas, Cambiar estado alumno, Confirmar asistencia. Focus trap obligatorio. |
| **KPICard** | `Icon` + `Label` + `Value` (Grande, Tabular nums) + `Trend` (Icon Up/Down/Neutral + % + Color) + `Sparkline` (Op, Mini Chart) | Dashboard Admin/Docente: Total Alumnos, % Asistencia, Morosidad, Cursos Activos. |
| **StatCard** | `Title` + `Metric` + `SubMetric` + `ProgressBar` (Circular o Lineal) + `StatusBadge` | Widget "Mi Carga Horaria" (Docente), "Progreso Matrícula" (Admin), "Pagos Pendientes" (Padre). |
| **UserMenuItem** | `Avatar` (Sm) + `Name` + `RoleBadge` + `Divider` + `MenuItems` (Perfil, Config, Tema, Cerrar Sesión) | Header Right. `RoleBadge` usa `EstadoAlumnoBadge` logic pero para Roles (ADMIN=primary, DOCENTE=success, etc). |
| **BreadcrumbItem** | `Link` / `CurrentPage` + `Separator` (Icon Chevron) + `Truncation` (Mobile: solo current + parent) | Navegación contextual en Admin/Docente (ej. Inicio / Horarios / Generador / 5to A). |
| **TabItem** | `Label` + `Icon` (Op) + `Badge` (Count/Alert) + `State` (Active, Hover, Disabled) + `Indicator` (Bottom Bar Animado) | Navegación principal Alumno/Padre (Resumen, Notas, Asistencia, Pagos, Docs). Admin/Docente: Tabs secundarias (Detalle/Historial). |
| **ChartTooltip** | `Header` (Label/X-Axis) + `Series[]` (Color Dot + Label + Value + % Diff) + `Footer` (Timestamp) | Wrapper `ngx-charts` / `Chart.js` unificado. Tema dark/light automático. |
| **WizardStepper** | `Steps[]` (Number/Icon + Label + State: Completed/Current/Pending/Error) + `ConnectingLine` + `Navigation` (Back, Next, Finish) | Importar Alumnos (3 pasos), Matrícula Online (4 pasos), Configuración Inicial Período (3 pasos). |

---

### 1.4. ORGANISMOS (Organismos) — Secciones Complejas e Independientes

| Organismo | Descripción Funcional | Componentes Internos (Moléculas/Átomos) | Reglas Críticas SGE |
|-----------|----------------------|-----------------------------------------|---------------------|
| **Header / TopBar** | Barra superior fija (64px desktop, 56px mobile). Logo + PeriodoSelector (Dropdown) + NotificationsBell (Badge Count + Dropdown Panel) + UserMenu (Avatar + Menu) + SidebarToggle (Mobile) | `PeriodoSelector` (Combobox async), `NotificationsPanel` (List `NotificationToast` + "Ver todas"), `UserMenu` (UserMenuItem) | **Z-Index:** 1000. **Responsive:** Mobile → Drawer lateral (Sidebar) + Header compacto. `PeriodoSelector` solo visible si >1 período activo. |
| **SidebarNavigation** | Navegación principal lateral (260px expandido, 72px colapsado). Grupos por Módulo. Badges de notificación en items. Tooltip en colapsado. Roving Tabindex accesible. | `NavGroup` (Accordion), `NavItem` (Icon + Label + Badge + Chevron), `NavDivider`, `BrandLogo` (Top), `UserInfo` (Bottom) | **Role-Based Rendering:** Items filtrados por `AuthService.permissions()`. Admin: Todos. Docente: Dashboard, Notas, Asistencia, Horario, Plan Clase. Alumno: Dashboard, Notas, Asistencia, Horario. Padre: Dashboard, Hijos, Pagos, Matrícula. |
| **DataTable** | Tabla de datos empresarial completa. Virtual Scroll (CDK) para >100 filas. Toolbar integrada. Inline Editing. Selección múltiple + Bulk Actions. Column Resizing/Reordering. Export (PDF/Excel/CSV). Skeleton Loading. Empty State. | `TableHeader`, `TableRow`, `PaginationControls`, `TableToolbar`, `InlineEditCell`, `RowExpansionPanel` | **Performance:** `trackBy: id`, `VirtualScrollViewport`. **Accesibilidad:** `role="grid"`, `aria-sort`, `aria-selected`. **Inline Edit:** Enter/Tab navega, Escape cancela, Auto-save debounce 800ms. |
| **ScheduleGrid** | Vista semanal (Lun-Vie, 08:00-15:00 configurable). 3 Vistas: Grado/Sección, Docente, Aula. Modo `view` / `edit`. Drag & Drop (CDK) con validación visual inmediata. Conflict Overlay (Borde rojo pulsante). Legend lateral filtrable. Export PDF/Excel. | `ScheduleCell`, `ScheduleLegend`, `ScheduleHeader`, `ScheduleToolbar`, `ConflictPanel`, `DragPreview` | **Algoritmo Colisión:** Frontend muestra conflictos conocidos (Backend). Drop → Optimistic UI → API `moverBloque` → Si 409 → Revert + Toast Error. **Responsive:** Tablet → Scroll X + Y fijos. Mobile → Vista "Lista por Día". |
| **DashboardWidgets** | Contenedor Grid (CSS Grid 12 col, Gap 24). Widgets arrastrables (CDK Drag Drop) → Persist layout en LocalStorage/Backend. Tipos: `KPICard`, `StatCard`, `ChartContainer`, `AlertList`, `QuickActions`, `RecentActivity`. | `WidgetWrapper` (Header: Title + Actions + DragHandle + Content + Skeleton) | **Admin:** KPIs Generales, Alertas Críticas, Rendimiento (Bar), Asistencia (Doughnut). **Docente:** Mis Cursos, Alertas Faltas, Acciones Rápidas. **Alumno:** Mi Horario (Mini Grid), Mis Notas (KPI), Alertas. **Padre:** Selector Hijos + Resumen por Hijo. |
| **WizardContainer** | Contenedor de pasos controlado. Validación por paso. Navegación (Back/Next/Finish). Persistencia estado (SessionStorage). Stepper Visual. Error Handling. | `WizardStepper`, `StepContent` (Dynamic Component), `WizardFooter` (Buttons), `StepValidationService` | **Importar Alumnos:** Paso 1 (Subir + Validar Headers) → Paso 2 (Preview Tabla + Errores Inline) → Paso 3 (Confirmar + Progreso + Resultado + Reporte PDF). |
| **ModalForm** | Modal accesible (Focus Trap, ARIA Dialog, Escape Close, Click Backdrop Close). Tamaños: `sm` (400px), `md` (600px), `lg` (900px), `xl` (1200px), `full` (95vw). Draggable. Footer Sticky. | `ModalHeader`, `ModalBody`, `ModalFooter`, `ModalOverlay` | **Usos:** Crear/Editar Docente, Asociar Padre, Transición Estado Alumno, Configurar Evaluación, Pago Online. |
| **AlumnoDetailTabs** | Vista detalle 360° del alumno. Tabs persistentes (URL Hash). Selector de Período. Datos sensibles solo Admin. | `TabNav` (TabItem + Badge Alertas), `TabPanel` (DatosPersonales, NotasTable+Chart, AsistCalendar, ScheduleGridRO, DocumentosList, HistorialEstadosGraph) | **HistorialEstadosGraph:** Visualizador del grafo de estados. Highlight camino actual. Tooltip transiciones. |
| **PrintLayouts** | Componentes puros para `@media print`. A4 (210x297mm) con márgenes 24mm. Header/Footer repetidos. Page-break control. | `PrintLibretaAlumno`, `PrintLibretaCurso`, `PrintHorario`, `PrintConstancia` | **Generación:** `ReporteService` → PDF (Jasper/OpenPDF) → Descarga. **Fallback:** Botón "Imprimir" → `window.print()`. |

---

### 1.5. PLANTILLAS (Templates / Page Layouts)

#### 1.5.1. Admin Layout (`/admin/*`)
- **Desktop (>1024px):** Sidebar expandido (260px). Header fijo (64px). Content centrado max 1440px con padding 24px.
- **Tablet (768-1024px):** Sidebar colapsado (72px, iconos-only). Hover/Click → Overlay expandido.
- **Mobile (<768px):** Sidebar → Drawer lateral (Hamburguesa en Header). Header 56px. Content padding 16px.

#### 1.5.2. Docente Layout (`/docente/*`)
- Sidebar colapsado por defecto (72px). Header compacto (56px, solo Período + Notif + User). Sin Breadcrumb complejo. Contexto: Curso Selector en Toolbar de cada feature.

#### 1.5.3. Alumno Layout (`/alumno/*`)
- Header simple (56px: Logo + Nombre Alumno + Avatar + Notif). TabNav sticky principal (scroll horizontal en mobile). **Bloqueo:** `EstadoActivoGuard` → si `estado !== ACTIVO`, muestra `EmptyState` + CTA "Contactar Admin".

#### 1.5.4. Padre Layout (`/padre/*`)
- Header (56px: Logo + Selector Hijos Pills + Notif + User). Selector Hijos: `MatChipList` horizontal con Avatar + Nombre + Badge Grado. Click → Cambia contexto del dashboard.

#### 1.5.5. Auth Layout (`/auth/*`)
- Centered card (400px max), shadow-lg, radius-xl. Logo + Tagline + Form + SSO Buttons + Links.

---

### 1.6. TOKENS DE DISEÑO (Design Tokens) — ESPECIFICACIÓN COMPLETA

#### 1.6.1. COLOR PALETTE (Paleta Institucional)

| Token | Light (Hex) | Dark (Hex) | Uso Semántico |
|-------|-------------|------------|---------------|
| **PRIMARIA (Azul Marino)** | | | |
| `color-primary-50` | `#EFF6FF` | `#1E3A5F` | Fondos hover, chips selected bg |
| `color-primary-100` | `#DBEAFE` | `#1E3A5F` | Fondos active, sidebar hover |
| `color-primary-200` | `#BFDBFE` | `#1E4A7F` | Bordes focus primary |
| `color-primary-300` | `#93C5FD` | `#2E6A9F` | Iconos disabled primary |
| `color-primary-400` | `#60A5FA` | `#3E8ACF` | Spinner primary |
| **`color-primary-500`** | **`#3B82F6`** | **`#60A5FA`** | **Botones Secondary/Outline, Links** |
| **`color-primary-600`** | **`#2563EB`** | **`#93C5FD`** | **Botones Primary Bg, Header Bg, Sidebar Active** |
| **`color-primary-700`** | **`#1D4ED8`** | **`#BFDBFE`** | **Botones Primary Hover** |
| **`color-primary-800`** | **`#1E3A8A`** | **`#DBEAFE`** | **Logo, Titulos Brand** |
| `color-primary-900` | `#1E2A5E` | `#EFF6FF` | Texto sobre bg primary-100 |

| Token | Light (Hex) | Dark (Hex) | Uso Semántico |
|-------|-------------|------------|---------------|
| **NEUTRA (Grises)** | | | |
| `color-neutral-0` | `#FFFFFF` | `#0F172A` | Superficie Base (Cards, Modals, Inputs) |
| `color-neutral-50` | `#F8FAFC` | `#111827` | Fondos Página (Body) Light |
| `color-neutral-100` | `#F1F5F9` | `#1E293B` | **Fondo Principal App**, Hover Table Row |
| `color-neutral-200` | `#E2E8F0` | `#334155` | Bordes Inputs, Cards, Dividers |
| `color-neutral-300` | `#CBD5E1` | `#475569` | Placeholders, Disabled Icons |
| `color-neutral-400` | `#94A3B8` | `#64748B` | Labels Secundarios, Hint Text |
| `color-neutral-500` | `#64748B` | `#94A3B8` | **Texto Secundario / Body Muted** |
| `color-neutral-600` | `#475569` | `#CBD5E1` | Texto Body Principal (Light) |
| `color-neutral-700` | `#334155` | `#E2E8F0` | **Texto Títulos / Headings** |
| `color-neutral-800` | `#1E293B` | `#F1F5F9` | Texto Strong |
| `color-neutral-900` | `#0F172A` | `#F8FAFC` | **Texto Principal (Dark mode)** |
| `color-neutral-950` | `#020617` | `#FCFCFD` | Fondos Dark Mode |

| Token | Light (Hex) | Dark (Hex) | Uso Semántico |
|-------|-------------|------------|---------------|
| **SEMÁNTICA (Feedback)** | | | |
| `color-success-50` | `#F0FDF4` | `#0A2A12` | Fondos Success |
| `color-success-500` | `#22C55E` | `#4ADE80` | Iconos Success, Badges `ACTIVO` |
| `color-success-600` | `#16A34A` | `#86EFAC` | **Botones Success, Texto Success** |
| `color-warning-50` | `#FFFBEB` | `#2A1F0A` | Fondos Warning (3 faltas) |
| `color-warning-500` | `#F59E0B` | `#FBBF24` | Iconos Warning, Badges `OBSERVADO` |
| `color-warning-600` | `#D97706` | `#FCD34D` | **Botones Warning, Texto Warning** |
| **`color-danger-50`** | **`#FEF2F2`** | **`#2A0A0A`** | **Fondos Danger / Alerta 5+ faltas** |
| **`color-danger-500`** | **`#EF4444`** | **`#F87171`** | **Iconos Danger, Badges `SUSPENDIDO`, `BAJA`** |
| **`color-danger-600`** | **`#DC2626`** | **`#FCA5A5`** | **Alertas Críticas, Errores Input, 5+ Faltas** |
| `color-info-50` | `#EFF6FF` | `#0A1F3A` | Fondos Info |
| `color-info-500` | `#3B82F6` | `#60A5FA` | Iconos Info, Badges `PRE_MATRICULADO` |

#### 1.6.2. TIPOGRAFÍA (Typography Scale)

| Token | Size | Weight | Line H | Uso |
|-------|------|--------|--------|-----|
| `display-xl` | 48px / 3rem | 700 Bold | 1.1 | Hero Login, Print Titles |
| `display-lg` | 36px / 2.25rem | 700 Bold | 1.2 | Page Titles (Dashboard) |
| `display-md` | 28px / 1.75rem | 600 Semibold | 1.2 | Section Titles, Card Titles |
| `display-sm` | 24px / 1.5rem | 600 Semibold | 1.3 | Subsection Titles, Modal Titles |
| `heading-xl` | 20px / 1.25rem | 600 Semibold | 1.3 | Widget Titles, Table Headers |
| `heading-lg` | 18px / 1.125rem | 600 Semibold | 1.4 | Sub-headers, Form Labels (lg) |
| `heading-md` | 16px / 1rem | 600 Semibold | 1.4 | **Body Strong**, Form Labels (md), Badge Text |
| `heading-sm` | 14px / 0.875rem | 600 Semibold | 1.4 | Small Labels, Chip Text, Tooltip Title |
| `body-lg` | 16px / 1rem | 400 Regular | 1.5 | **Body Principal** (Paragraphs, Table Cells, Inputs) |
| `body-md` | 14px / 0.875rem | 400 Regular | 1.5 | Body Small (Meta, Hint, Help Text) |
| `body-sm` | 12px / 0.75rem | 400 Regular | 1.5 | Caption, Timestamp, Footer |
| `code` | 13px / 0.8125rem | 400 Regular | 1.6 | JetBrains Mono. Códigos Estudiante/Docente |

**Font Family:** `Inter` (Variable) para UI principal. `JetBrains Mono` para códigos.  
**Numbers:** `font-variant-numeric: tabular-nums` en KPIs, Notas, Porcentajes.

#### 1.6.3. ESPACIADO (Spacing)

| Token | Valor | Uso |
|-------|-------|-----|
| `space-1` | 4px / 0.25rem | Gap icono/texto en botones, padding chips |
| `space-2` | 8px / 0.5rem | Gap FormField (label-input), padding sm cards |
| `space-3` | 12px / 0.75rem | Padding Input md, Gap Molecules |
| **`space-4`** | **16px / 1rem** | **Base Unit.** Padding Cards/Containers, Margin Bottom |
| `space-5` | 20px / 1.25rem | Padding lg (Modals) |
| **`space-6`** | **24px / 1.5rem** | **Grid Gap Desktop**, Padding Page Content |
| `space-8` | 32px / 2rem | Gap entre secciones mayores |
| `space-12` | 48px / 3rem | Max Width Containers |
| `space-16` | 64px / 4rem | Espaciado vertical grande |

**Border Radius:** `none`=0, `sm`=4px, `md`=8px (default), `lg`=12px, `xl`=16px, `full`=9999px

**Sombras:** `xs`=0 1px 2px / 0.05, `sm`=0 1px 3px / 0.1, `md`=0 4px 6px / 0.1, `lg`=0 10px 15px / 0.1, `xl`=0 20px 25px / 0.1. `focus`=0 0 0 3px primary-200, `danger-focus`=0 0 0 3px danger-200.

**Z-Index:** `base`=0, `sticky`=10, `dropdown`=100, `drawer`=200, `header`=1000, `modal-overlay`=1100, `modal`=1200, `toast`=1300, `tooltip`=1400

**Breakpoints:** `sm`=640px, `md`=768px, `lg`=1024px, `xl`=1280px, `2xl`=1536px

**Transiciones:** `easing-standard`=cubic-bezier(0.4,0,0.2,1), `emphasized`=cubic-bezier(0.4,0,0.6,1), `decelerated`=cubic-bezier(0,0,0.2,1). `fast`=150ms, `normal`=250ms, `slow`=350ms.

---

### 1.7. ESPECIFICACIÓN VISUAL DE COMPONENTES CRÍTICOS

#### 1.7.1. Tarjetas de Alerta por Inasistencias Consecutivas (`AlertCard`)

| Elemento | Especificación |
|----------|----------------|
| **Contenedor** | `SgeCard` variant `bordered` + `border-left: 4px solid danger-600` (3 faltas) / `danger-700` (5+). `bg: danger-50` (Light) / `danger-950/20` (Dark). `shadow-sm`. `radius-md`. |
| **Header** | `HStack`: `Icon` (AlertTriangle, danger-600, 20px) + `VStack`: `Title` ("Alerta Inasistencias", heading-sm) + `Subtitle` ("Matemática - 3ro A", body-sm). `Badge` ("3 FALTAS" / "5+ CRÍTICO", variant: danger). |
| **Cuerpo** | `Avatar Alumno` (md, status ring `offline`) + `VStack`: Nombre (heading-md), Código (body-sm), Detalle Fechas Faltas (body-sm). `ProgressBar` (lineal, color-danger-500). |
| **Acciones** | "Ver Detalle" (ghost), "Notificar Padre" (outline primary), "Marcar Vista" (primary). |
| **Vacío** | `SgeEmptyState`: Icon CheckCircle (success-500), "Sin alertas activas", "Todos los alumnos tienen asistencia regular". |

#### 1.7.2. Grilla de Horarios (`ScheduleGrid`) — Estados de Celda

| Estado | Fondo | Borde | Interacción |
|--------|-------|-------|-------------|
| **Libre** | transparent | dashed neutral-300 | Drop Zone activo en edit. Hover → bg primary-50 |
| **Ocupada (1 bloque)** | materia-color-100 | solid materia-color-300 | Click → ModalDetalle. Drag (edit) → CdkDrag |
| **Ocupada (2+)** | materia-color-100 | solid materia-color-300 | Apilados 50% c/u. Chip `+N más` si >2 |
| **Conflicto** | danger-50 | **2px solid danger-600 + pulse-border 1.5s** | Drop disabled. Tooltip persistente detalle conflicto |
| **Drag Over (válido)** | primary-100 | dashed primary-500 | "Soltar aquí" |
| **Drag Over (inválido)** | danger-50 | dashed danger-500 | "Conflicto detectado" |

```scss
@keyframes pulse-border {
  0%, 100% { border-color: #DC2626; box-shadow: 0 0 0 0 rgba(220,38,38,0.4); }
  50% { border-color: #EF4444; box-shadow: 0 0 0 4px rgba(220,38,38,0); }
}
```

#### 1.7.3. Tabla de Notas con Edición en Línea

| Elemento | Especificación |
|----------|----------------|
| **Cabecera** | `sticky top:0; z-index:10; bg: neutral-0; border-bottom:2px solid neutral-200`. Sortable: Icon Chevron. |
| **Fila (read)** | `bg: neutral-0` (par) / `neutral-25` (impar). Hover: `bg: neutral-100`. `border-bottom: 1px solid neutral-200`. |
| **Celda Nota (read)** | `tabular-nums`, `font-weight:500`. Color: ≥14 `success-600`, 11-13 `warning-600`, <11 `danger-600`. Center. |
| **Celda Nota (edit)** | `SgeNumberInput` width 70px, min=0 max=20 step=0.5. Enter/Tab → save + focus next. Escape → cancel. Auto-save 800ms. |
| **Celda Promedio** | `font-weight:600`, `bg: neutral-100`. Tooltip desglose ponderación. |
| **Fila editando** | `bg: primary-50`, `box-shadow: inset 3px 0 0 primary-600` (left border). |
| **Toolbar** | Sticky top (debajo Header). Search + Select Bimestre + Select Evaluación + Importar Excel + Publicar. |

---

## 2. MAQUETACIÓN EN ANGULAR — ARQUITECTURA SCSS

### 2.1. ESTRUCTURA DE CARPETAS `src/styles/`

```
src/styles/
├── _variables.scss      # Tokens de diseño (Figma → SCSS) - SOLO VARIABLES
├── _mixins.scss         # Mixins funcionales (Responsive, Layout, Utils)
├── _base.scss           # Reset, Tipografía base, Estilos globales
├── _themes.scss         # Configuración de Temas (Light/Dark) via CSS Custom Props
└── styles.scss          # ENTRY POINT - Orden de importación crítico
```

> **Regla de Oro:** Los componentes Angular (`*.component.scss`) **NUNCA** definen valores hardcodeados. Solo consumen variables (`$color-primary-600`) y mixins (`@include respond-to(md)`).

---

### 2.2. `_variables.scss` — MAPEO 1:1 TOKENS FIGMA

```scss
// src/styles/_variables.scss
// =============================================================================
// TOKENS DE DISEÑO - SGE (Generados desde Figma Tokens / Style Dictionary)
// =============================================================================

// -----------------------------------------------------------------------------
// COLOR PALETTE
// -----------------------------------------------------------------------------
$color-primary-50  : #EFF6FF;
$color-primary-100 : #DBEAFE;
$color-primary-200 : #BFDBFE;
$color-primary-300 : #93C5FD;
$color-primary-400 : #60A5FA;
$color-primary-500 : #3B82F6;
$color-primary-600 : #2563EB;
$color-primary-700 : #1D4ED8;
$color-primary-800 : #1E3A8A;
$color-primary-900 : #1E2A5E;
$color-primary-950 : #17203F;

$color-neutral-0   : #FFFFFF;
$color-neutral-25  : #FCFCFD;
$color-neutral-50  : #F8FAFC;
$color-neutral-100 : #F1F5F9;
$color-neutral-200 : #E2E8F0;
$color-neutral-300 : #CBD5E1;
$color-neutral-400 : #94A3B8;
$color-neutral-500 : #64748B;
$color-neutral-600 : #475569;
$color-neutral-700 : #334155;
$color-neutral-800 : #1E293B;
$color-neutral-900 : #0F172A;
$color-neutral-950 : #020617;

$color-success-50  : #F0FDF4;
$color-success-500 : #22C55E;
$color-success-600 : #16A34A;
$color-success-700 : #15803D;

$color-warning-50  : #FFFBEB;
$color-warning-500 : #F59E0B;
$color-warning-600 : #D97706;
$color-warning-700 : #B45309;

$color-danger-50   : #FEF2F2;
$color-danger-500  : #EF4444;
$color-danger-600  : #DC2626;
$color-danger-700  : #B91C1C;

$color-info-50     : #EFF6FF;
$color-info-500    : #3B82F6;
$color-info-600    : #2563EB;

// -----------------------------------------------------------------------------
// SEMANTIC COLOR ALIASES (USAR ESTOS EN COMPONENTES)
// -----------------------------------------------------------------------------
$color-bg-app          : $color-neutral-100;
$color-bg-surface      : $color-neutral-0;
$color-bg-surface-hover: $color-neutral-50;
$color-bg-input        : $color-neutral-0;
$color-bg-primary      : $color-primary-600;
$color-bg-primary-hover: $color-primary-700;
$color-bg-danger       : $color-danger-600;
$color-bg-danger-hover : $color-danger-700;

$color-text-primary    : $color-neutral-900;
$color-text-secondary  : $color-neutral-500;
$color-text-on-primary : $color-neutral-0;
$color-text-on-danger  : $color-neutral-0;
$color-text-danger     : $color-danger-600;
$color-text-success    : $color-success-600;
$color-text-warning    : $color-warning-600;
$color-text-link       : $color-primary-600;

$color-border-default  : $color-neutral-200;
$color-border-input    : $color-neutral-300;
$color-border-focus    : $color-primary-500;
$color-border-error    : $color-danger-500;
$color-border-danger   : $color-danger-600;

$color-state-activo       : $color-success-600;
$color-state-suspendido   : $color-danger-600;
$color-state-prematricula : $color-info-600;
$color-state-egresado     : $color-primary-700;
$color-state-retirado     : $color-neutral-500;
$color-state-baja         : $color-danger-700;
$color-state-archivado    : $color-neutral-400;

$color-alerta-3-faltas    : $color-warning-600;
$color-alerta-5-faltas    : $color-danger-600;

// -----------------------------------------------------------------------------
// TYPOGRAPHY
// -----------------------------------------------------------------------------
$font-family-sans      : 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
$font-family-mono      : 'JetBrains Mono', 'Fira Code', monospace;

$fs-display-xl : 3rem;
$fs-display-lg : 2.25rem;
$fs-display-md : 1.75rem;
$fs-display-sm : 1.5rem;
$fs-heading-xl : 1.25rem;
$fs-heading-lg : 1.125rem;
$fs-heading-md : 1rem;
$fs-heading-sm : 0.875rem;
$fs-body-lg    : 1rem;
$fs-body-md    : 0.875rem;
$fs-body-sm    : 0.75rem;
$fs-code       : 0.8125rem;

$fw-regular : 400;
$fw-medium  : 500;
$fw-semibold: 600;
$fw-bold    : 700;

$lh-tight  : 1.1;
$lh-snug   : 1.2;
$lh-normal : 1.5;
$lh-relaxed: 1.6;

$ls-tight  : -0.02em;
$ls-normal : 0;
$ls-wide   : 0.01em;

// -----------------------------------------------------------------------------
// SPACING
// -----------------------------------------------------------------------------
$space-0 : 0;
$space-1 : 0.25rem;
$space-2 : 0.5rem;
$space-3 : 0.75rem;
$space-4 : 1rem;
$space-5 : 1.25rem;
$space-6 : 1.5rem;
$space-8 : 2rem;
$space-10: 2.5rem;
$space-12: 3rem;
$space-16: 4rem;

// -----------------------------------------------------------------------------
// BORDER RADIUS
// -----------------------------------------------------------------------------
$radius-none : 0;
$radius-sm   : 0.25rem;
$radius-md   : 0.5rem;
$radius-lg   : 0.75rem;
$radius-xl   : 1rem;
$radius-full : 9999px;

// -----------------------------------------------------------------------------
// SHADOWS
// -----------------------------------------------------------------------------
$shadow-xs  : 0 1px 2px 0 rgb(0 0 0 / 0.05);
$shadow-sm  : 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
$shadow-md  : 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
$shadow-lg  : 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
$shadow-xl  : 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
$shadow-focus       : 0 0 0 3px #BFDBFE;
$shadow-danger-focus: 0 0 0 3px #FCA5A5;

// -----------------------------------------------------------------------------
// Z-INDEX
// -----------------------------------------------------------------------------
$z-base        : 0;
$z-sticky      : 10;
$z-dropdown    : 100;
$z-drawer      : 200;
$z-header      : 1000;
$z-modal-overlay: 1100;
$z-modal       : 1200;
$z-toast       : 1300;
$z-tooltip     : 1400;

// -----------------------------------------------------------------------------
// BREAKPOINTS
// -----------------------------------------------------------------------------
$bp-sm  : 640px;
$bp-md  : 768px;
$bp-lg  : 1024px;
$bp-xl  : 1280px;
$bp-2xl : 1536px;

// -----------------------------------------------------------------------------
// TRANSITIONS
// -----------------------------------------------------------------------------
$ease-standard    : cubic-bezier(0.4, 0, 0.2, 1);
$ease-emphasized  : cubic-bezier(0.4, 0, 0.6, 1);
$ease-decelerated : cubic-bezier(0, 0, 0.2, 1);
$duration-fast    : 150ms;
$duration-normal  : 250ms;
$duration-slow    : 350ms;
$transition-fast  : all $duration-fast $ease-standard;
$transition-normal: all $duration-normal $ease-standard;
$transition-slow  : all $duration-slow $ease-emphasized;

// -----------------------------------------------------------------------------
// LAYOUT CONSTANTS
// -----------------------------------------------------------------------------
$header-height       : 3.5rem;
$header-height-lg    : 4rem;
$sidebar-width       : 16rem;
$sidebar-collapsed-w : 4.5rem;
$container-max-lg    : 90rem;
$container-max-md    : 75rem;
```

---

### 2.3. `_mixins.scss` — RESPONSIVE, LAYOUT Y UTILIDADES

```scss
// src/styles/_mixins.scss
// =============================================================================
// MIXINS FUNCIONALES
// =============================================================================

@use 'sass:map';

$breakpoints: (
  'sm'  : $bp-sm,
  'md'  : $bp-md,
  'lg'  : $bp-lg,
  'xl'  : $bp-xl,
  '2xl' : $bp-2xl
) !default;

// RESPONSIVE (Mobile First)
@mixin respond-to($breakpoint) {
  @if map.has-key($breakpoints, $breakpoint) {
    @media (min-width: map.get($breakpoints, $breakpoint)) { @content; }
  }
}

@mixin respond-to-max($breakpoint) {
  @if map.has-key($breakpoints, $breakpoint) {
    @media (max-width: map.get($breakpoints, $breakpoint) - 1px) { @content; }
  }
}

// FLEXBOX HELPERS
@mixin flex-center { display: flex; align-items: center; justify-content: center; }
@mixin flex-between { display: flex; align-items: center; justify-content: space-between; }
@mixin flex-center-column($gap: $space-4) { display: flex; flex-direction: column; align-items: center; gap: $gap; }
@mixin flex-col($gap: $space-4) { display: flex; flex-direction: column; gap: $gap; }
@mixin inline-flex-center($gap: $space-2) { display: inline-flex; align-items: center; gap: $gap; }

// GRID HELPERS
@mixin grid-cols($cols, $gap: $space-6) { display: grid; grid-template-columns: repeat($cols, 1fr); gap: $gap; }
@mixin grid-auto-fit($min-width, $gap: $space-6) { display: grid; grid-template-columns: repeat(auto-fit, minmax($min-width, 1fr)); gap: $gap; }

// Schedule Grid Specific
@mixin schedule-grid($hour-count: 8, $day-count: 5, $gap: 1px) {
  display: grid;
  grid-template-columns: 3.5rem repeat($day-count, 1fr);
  grid-template-rows: 2.5rem repeat($hour-count, minmax(3.5rem, auto));
  gap: $gap;
  border: 1px solid $color-border-default;
  border-radius: $radius-md;
  overflow: hidden;
  background: $color-bg-surface;
}

// TYPOGRAPHY SHORTHANDS
@mixin text-display($size: xl) { font-family: $font-family-display; font-weight: $fw-bold; ... }
@mixin text-heading($size: md) { font-family: $font-family-sans; font-weight: $fw-semibold; ... }
@mixin text-body($size: lg) { font-family: $font-family-sans; font-weight: $fw-regular; ... }
@mixin text-code { font-family: $font-family-mono; font-size: $fs-code; font-variant-numeric: tabular-nums; }

// VISUAL STATES
@mixin focus-visible-ring($color: $color-border-focus) {
  &:focus-visible { outline: none; box-shadow: 0 0 0 3px $color; }
}

@mixin disabled-state {
  opacity: 0.5; cursor: not-allowed; pointer-events: none;
}

// TRUNCATION
@mixin truncate($lines: 1) {
  @if $lines == 1 { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  @else { display: -webkit-box; -webkit-line-clamp: $lines; -webkit-box-orient: vertical; overflow: hidden; }
}

// PRINT
@mixin print-only { @media print { @content; } }
@mixin page-break-inside-avoid { @media print { break-inside: avoid; } }
```

---

### 2.4. `_base.scss` — RESET, GLOBALES Y TIPOGRAFÍA BASE

```scss
// src/styles/_base.scss
// =============================================================================
// ESTILOS GLOBALES, RESET Y TIPOGRAFÍA BASE
// =============================================================================

@use './variables' as *;

// RESET
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

html {
  font-size: 16px;
  scroll-behavior: smooth;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  font-family: $font-family-sans;
  font-size: $fs-body-lg;
  line-height: $lh-normal;
  background-color: $color-bg-app;
  color: $color-text-primary;
  min-height: 100vh;
}

img { display: block; max-width: 100%; }
a { color: $color-text-link; text-decoration: none; &:hover { color: $color-primary-700; } }
ul, ol { list-style: none; }
table { border-collapse: collapse; width: 100%; }

// FOCUS VISIBLE
:focus:not(:focus-visible) { outline: none; }
:focus-visible { outline: none; box-shadow: $shadow-focus; border-radius: $radius-sm; }
::selection { background-color: $color-primary-200; color: $color-primary-900; }

// UTILITY CLASSES
.sr-only { position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(0,0,0,0); white-space: nowrap; border: 0; }
.text-danger { color: $color-text-danger !important; }
.text-success { color: $color-text-success !important; }
.bg-surface { background-color: $color-bg-surface !important; }
.p-4 { padding: $space-4; }

// FORM BASE
input, textarea, select {
  width: 100%; padding: $space-2 $space-3; font-size: $fs-body-md;
  border: 1px solid $color-border-input; border-radius: $radius-sm;
  transition: border-color $duration-fast, box-shadow $duration-fast;
  &:focus { border-color: $color-border-focus; box-shadow: $shadow-focus; outline: none; }
  &:disabled { @include disabled-state; }
  &[aria-invalid="true"] { border-color: $color-border-error; &:focus { box-shadow: $shadow-danger-focus; } }
}
label { font-size: $fs-body-sm; font-weight: $fw-medium; display: block; margin-bottom: $space-1; }

// KEYFRAMES
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }
@keyframes pulse-border {
  0%, 100% { border-color: $color-danger-600; box-shadow: 0 0 0 0 rgba($color-danger-500, 0.4); }
  50% { border-color: $color-danger-500; box-shadow: 0 0 0 4px rgba($color-danger-500, 0); }
}
@keyframes fade-in { from { opacity: 0; } to { opacity: 1; } }
@keyframes slide-up { from { opacity: 0; transform: translateY(1rem); } to { opacity: 1; transform: translateY(0); } }

.animate-spin { animation: spin 1s linear infinite; }
.animate-fade-in { animation: fade-in 250ms ease-out; }
.animate-slide-up { animation: slide-up 250ms ease-out; }
```

---

### 2.5. `_themes.scss` — SOPORTE DARK MODE (CSS CUSTOM PROPERTIES)

```scss
// src/styles/_themes.scss
// =============================================================================
// TEMAS: LIGHT (Default) / DARK (via class .dark en <html> o data-theme)
// =============================================================================

@use './variables' as *;

:root {
  --sge-bg-app: #{$color-bg-app};
  --sge-bg-surface: #{$color-bg-surface};
  --sge-bg-primary: #{$color-bg-primary};
  --sge-bg-primary-hover: #{$color-bg-primary-hover};
  --sge-bg-danger: #{$color-bg-danger};
  --sge-bg-success: #{$color-bg-success};
  --sge-bg-warning: #{$color-bg-warning};

  --sge-text-primary: #{$color-text-primary};
  --sge-text-secondary: #{$color-text-secondary};
  --sge-text-on-primary: #{$color-text-on-primary};
  --sge-text-danger: #{$color-text-danger};
  --sge-text-success: #{$color-text-success};
  --sge-text-warning: #{$color-text-warning};
  --sge-text-link: #{$color-text-link};

  --sge-border-default: #{$color-border-default};
  --sge-border-input: #{$color-border-input};
  --sge-border-focus: #{$color-border-focus};
  --sge-border-error: #{$color-border-error};
  --sge-border-danger: #{$color-border-danger};

  --sge-shadow-sm: #{$shadow-sm};
  --sge-shadow-md: #{$shadow-md};
  --sge-shadow-focus: #{$shadow-focus};
  --sge-shadow-danger-focus: #{$shadow-danger-focus};

  --sge-state-activo: #{$color-state-activo};
  --sge-state-suspendido: #{$color-state-suspendido};
  --sge-state-alerta-3: #{$color-alerta-3-faltas};
  --sge-state-alerta-5: #{$color-alerta-5-faltas};
}

.dark, [data-theme="dark"] {
  --sge-bg-app: #{$color-neutral-950};
  --sge-bg-surface: #{$color-neutral-900};
  --sge-bg-primary: #{$color-primary-500};
  --sge-bg-primary-hover: #{$color-primary-400};
  --sge-bg-danger: #{$color-danger-500};
  --sge-bg-success: #{$color-success-500};
  --sge-bg-warning: #{$color-warning-500};

  --sge-text-primary: #{$color-neutral-50};
  --sge-text-secondary: #{$color-neutral-400};
  --sge-text-on-primary: #{$color-neutral-950};
  --sge-text-danger: #{$color-danger-300};
  --sge-text-success: #{$color-success-300};
  --sge-text-warning: #{$color-warning-300};
  --sge-text-link: #{$color-primary-400};

  --sge-border-default: #{$color-neutral-700};
  --sge-border-input: #{$color-neutral-600};
  --sge-border-focus: #{$color-primary-400};
  --sge-border-error: #{$color-danger-400};
  --sge-border-danger: #{$color-danger-500};

  --sge-shadow-sm: 0 1px 3px 0 rgb(0 0 0 / 0.3), 0 1px 2px -1px rgb(0 0 0 / 0.2);
  --sge-shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.3), 0 2px 4px -2px rgb(0 0 0 / 0.2);
  --sge-shadow-focus: 0 0 0 3px #{$color-primary-800};
  --sge-shadow-danger-focus: 0 0 0 3px #{$color-danger-900};

  --sge-state-activo: #{$color-success-400};
  --sge-state-suspendido: #{$color-danger-400};
  --sge-state-alerta-3: #{$color-warning-400};
  --sge-state-alerta-5: #{$color-danger-400};
}

@mixin css-var($property, $var-name, $fallback: null) {
  #{$property}: if($fallback, $fallback, var(#{$var-name}));
}
```

---

### 2.6. `styles.scss` — ENTRY POINT Y ORDEN DE IMPORTACIÓN

```scss
// src/styles.scss
// =============================================================================
// ORDEN CRÍTICO: Variables -> Mixins -> Themes (CSS Vars) -> Base
// =============================================================================

@use './styles/variables' as *;
@use './styles/mixins' as *;
@use './styles/themes';
@use './styles/base';

@import '@fortawesome/fontawesome-free/css/all.min.css';

html, body, app-root { height: 100%; display: flex; flex-direction: column; }
app-root { flex: 1; display: flex; flex-direction: column; }

@media print {
  .no-print { display: none !important; }
  body { background: white; color: black; }
  .shadow, .shadow-lg { box-shadow: none !important; }
}
```

---

### 2.7. USO MODULAR EN COMPONENTE ANGULAR: EJEMPLO `alerta-inasistencia.component.scss`

```scss
// src/app/shared/components/alert/alerta-inasistencia.component.scss
// =============================================================================
// USO EXCLUSIVO DE VARIABLES Y MIXINS
// =============================================================================

@use '../../../../styles/variables' as *;
@use '../../../../styles/mixins' as *;

.alert-card {
  @include flex-col($space-4);
  padding: $space-4 $space-5;
  background-color: $color-bg-surface;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
  border: 1px solid $color-border-default;
  border-left-width: 4px;
  border-left-style: solid;
  transition: box-shadow $duration-normal;

  &--warning {
    border-left-color: $color-alerta-3-faltas;
    background-color: $color-warning-50;
  }

  &--danger,
  &--critical {
    border-left-color: $color-alerta-5-faltas;
    background-color: $color-danger-50;
  }

  &--critical { animation: pulse-border 2s infinite ease-in-out; }

  &__header {
    @include flex-between;
    gap: $space-3;
    width: 100%;
  }

  &__icon {
    @include inline-flex-center;
    width: 2.5rem; height: 2.5rem;
    border-radius: $radius-full;
    flex-shrink: 0;
    background-color: color.scale($color-border-default, $lightness: 10%);
  }

  &__title-group {
    @include flex-col($space-1);
    flex: 1; min-width: 0;
  }

  &__title {
    @include text-heading(md);
    color: $color-text-primary;
    @include truncate;
  }

  &__subtitle {
    @include text-body(sm);
    color: $color-text-secondary;
    @include truncate;
  }

  &__body {
    display: flex; align-items: flex-start;
    gap: $space-4; width: 100%;
  }

  &__info { @include flex-col($space-1); flex: 1; min-width: 0; }

  &__name { @include text-heading(sm); color: $color-text-primary; @include truncate; }
  &__code { @include text-body(sm); color: $color-text-secondary; @include text-code; }
  &__detail { @include text-body(sm); color: $color-text-secondary; @include truncate(2); font-style: italic; }

  &__progress {
    margin-top: $space-2;
    height: 0.5rem;
    background-color: $color-neutral-200;
    border-radius: $radius-full;
    overflow: hidden;
    width: 100%;
    max-width: 12rem;
  }

  &__progress-bar {
    height: 100%;
    border-radius: $radius-full;
    transition: width $duration-slow $ease-emphasized;
  }

  &__actions {
    @include flex-between;
    width: 100%;
    padding-top: $space-3;
    margin-top: $space-2;
    border-top: 1px solid $color-border-default;
    flex-wrap: wrap;
    gap: $space-2;
    justify-content: flex-end;
  }

  &:hover { box-shadow: $shadow-md; }
  &:focus-within { @include focus-visible-ring; }
}
```

---

### 2.8. CHECKLIST DE INTEGRACIÓN SCSS → ANGULAR

| Verificación | Detalle |
|-------------|---------|
| **`styleUrl` vs `styles`** | Usar `styleUrl: './componente.component.scss'` (archivo externo) para caché del navegador. |
| **`@use` vs `@import`** | Usar **`@use`** (Sass Modules). Namespacing con `as *` evita colisiones. |
| **`OnPush`** | `ChangeDetectionStrategy.OnPush` obligatorio en todos los componentes Shared y Features. |
| **Variables en Componentes** | Crear barrel `src/app/core/styles/index.scss` que haga `@forward` de `_variables` y `_mixins`. Configurar `tsconfig.json` `paths` para evitar rutas profundas (`../../../../styles/variables`). |
| **CSS Custom Props (Themes)** | Componentes críticos (Header, Sidebar, Modals) usar `var(--sge-bg-surface)` vía `@include css-var(...)` para Dark Mode dinámico. |
| **Build Production** | `ng build --configuration production` → `styles.<hash>.scss` minificado, sourcemaps deshabilitados. |
| **Linting** | `stylelint` con regla `use-variable` para forzar uso de tokens SCSS en vez de valores hardcodeados. |
| **Accesibilidad** | Contraste AA (4.5:1 texto, 3:1 UI large text). Focus visible en todos los elementos interactivos. ARIA roles en componentes complejos (grid, tablist, dialog). |
