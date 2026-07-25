export interface LoginRequest { username: string; password: string }
export interface RegisterRequest { username: string; email: string; password: string; personaId?: number }
export interface AuthResponse { token: string; refreshToken: string; username: string; email: string; roles: string[] }
export interface RefreshRequest { refreshToken: string }
export interface ForgotPasswordRequest { email: string }
export interface ResetPasswordRequest { email: string; codigo: string; nuevaPassword: string }

export interface PeriodoAcademico { id?: number; nombre: string; codigo: string; fechaInicio: string; fechaFin: string; estado: string }
export interface Alumno { id?: number; nombres: string; apellidos: string; dni: string; fechaNac?: string; genero?: string; telefono?: string; direccion?: string; codigoEstudiante: string; estadoActualId?: number; estadoActualCodigo?: string; estadoActualNombre?: string; subEstado?: string; fechaIngreso?: string }
export interface Docente { id?: number; nombres: string; apellidos: string; dni: string; codigoEmpleado: string; especialidad: string }
export interface Curso { id?: number; estado: string; periodoId: number; periodoNombre: string; gradoId: number; gradoNombre: string; seccionId: number; seccionNombre: string; materiaId: number; materiaNombre: string; docenteId?: number; docenteNombre?: string; aulaId?: number; aulaNombre?: string }
export interface Grado { id?: number; nombre: string; nivel: string; orden: number; capacidadMax?: number }
export interface Seccion { id?: number; nombre: string; capacidad: number; gradoId: number }
export interface Aula { id?: number; codigo: string; nombre: string; tipo: string; capacidad: number }
export interface Materia { id?: number; codigo: string; nombre: string; tipo: string; horasSemanalesReq?: number }
export interface HorarioBloque { id?: number; cursoId: number; diaSemana: number; horaInicio: string; horaFin: string; aulaId?: number }
export interface Bimestre { id?: number; periodoId: number; nombre: string; numero: number; fechaInicio: string; fechaFin: string; estado: string }
export interface Evaluacion { id?: number; nombre: string; fecha: string; ponderacionOverride?: number; cursoId: number; cursoNombre?: string; bimestreId: number; bimestreNombre?: string; tipoEvaluacionId: number; tipoEvaluacionNombre?: string }
export interface Nota { id?: number; valor: number; observacion?: string; evaluacionId: number; evaluacionNombre?: string; alumnoId: number; alumnoNombre?: string }
export interface SesionClase { id?: number; cursoId: number; fecha: string; horaInicio?: string; horaFin?: string; tema?: string; estado: string; horarioBloqueId?: number }
export interface Asistencia { id?: number; sesionId: number; alumnoId: number; tipoAsistencia: string; minutosTardanza?: number; observacion?: string }
export interface ConceptoPago { id?: number; nombre: string; montoBase: number; periodicidad?: string; gradoId?: number }
export interface CronogramaPago { id?: number; alumnoId: number; alumnoNombre?: string; conceptoPagoId: number; conceptoNombre?: string; periodoId: number; fechaVencimiento: string; monto: number; estado: string }
export interface Pago { id?: number; alumnoId: number; alumnoNombre?: string; cronogramaPagoId: number; monto: number; metodo: string; referencia: string; fechaPago: string }
export interface TipoEvaluacion { id?: number; nombre: string; pesoPorcentaje: number; orden: number }
export interface Padre { id?: number; nombres: string; apellidos: string; dni: string; fechaNac?: string; genero?: string; telefono?: string; direccion?: string; parentesco?: string; esTitular?: boolean }
export interface AlertaFaltas { id?: number; alumnoId: number; alumnoNombre?: string; cantidadConsecutivas: number; nivel: string; estado: string; fechas?: string; resuelta: boolean }
export interface Usuario { id?: number; username: string; email: string; enabled: boolean; personaId?: number; personaNombre?: string; roles: string[] }
export interface Rol { id?: number; codigo: string; nombre: string; permisos: string[] }
export interface Permiso { id?: number; codigo: string; descripcion: string; modulo?: string }
export interface Notificacion { id?: number; titulo: string; cuerpo: string; usuarioId: number; tipo: string; leida: boolean; createdAt?: string }
export interface NotificacionCreateRequest { usuarioId: number; titulo: string; cuerpo: string; tipo?: string; entidadTipo?: string; entidadId?: number }
export interface TransicionEstado { id?: number; estadoOrigenId: number; estadoOrigenNombre?: string; estadoDestinoId: number; estadoDestinoNombre?: string; codigoGatillante: string; esAutomatica?: boolean; requiereAdmin: boolean; requiereConsejo?: boolean; notificaPadre: boolean }
export interface EstadoAlumno { id?: number; codigo: string; nombre: string; esTerminal: boolean; esTransitorio: boolean; permisoAcceso: boolean }
export interface HistorialEstado { id?: number; estadoOrigenCodigo?: string; estadoDestinoCodigo: string; estadoDestinoNombre?: string; motivo?: string; fechaCambio?: string; registradoPor?: string; referenciaDocumento?: string }
export interface CambioEstadoRequest { alumnoId: number; estadoCodigo: string; motivo?: string; referenciaDocumento?: string }
export interface Documento { id?: number; nombreArchivo: string; tipoDoc: string; mimeType?: string; entidadTipo: string; entidadId: number }
export interface ReporteRequest { cursoId: number; bimestreId: number }
export interface StripePaymentIntentRequest { cronogramaPagoId: number; alumnoId: number; usuarioId: number }
export interface StripePaymentIntentResponse { clientSecret: string; paymentIntentId: string; amount: number; currency: string }
export interface Page<T> { content: T[]; totalElements: number; totalPages: number; size: number; number: number }
