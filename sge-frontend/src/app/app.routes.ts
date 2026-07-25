import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/auth.guard';
import { Layout } from './layout/layout';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./auth/login/login').then(m => m.Login), canActivate: [loginGuard] },
  { path: 'register', loadComponent: () => import('./auth/register/register').then(m => m.Register), canActivate: [loginGuard] },
  { path: 'forgot-password', loadComponent: () => import('./auth/forgot-password/forgot-password').then(m => m.ForgotPassword), canActivate: [loginGuard] },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', loadComponent: () => import('./dashboard/dashboard').then(m => m.Dashboard) },
      { path: 'periodos', loadComponent: () => import('./features/periodos/periodos-list').then(m => m.PeriodosList), canActivate: [adminGuard] },
      { path: 'alumnos', loadComponent: () => import('./features/alumnos/alumnos-list').then(m => m.AlumnosList) },
      { path: 'grados', loadComponent: () => import('./features/grados/grados-list').then(m => m.GradosList) },
      { path: 'secciones', loadComponent: () => import('./features/secciones/secciones-list').then(m => m.SeccionesList) },
      { path: 'docentes', loadComponent: () => import('./features/docentes/docentes-list').then(m => m.DocentesList) },
      { path: 'materias', loadComponent: () => import('./features/materias/materias-list').then(m => m.MateriasList) },
      { path: 'aulas', loadComponent: () => import('./features/aulas/aulas-list').then(m => m.AulasList) },
      { path: 'cursos', loadComponent: () => import('./features/cursos/cursos-list').then(m => m.CursosList) },
      { path: 'bimestres', loadComponent: () => import('./features/bimestres/bimestres-list').then(m => m.BimestresList) },
      { path: 'tipos-evaluacion', loadComponent: () => import('./features/tipos-evaluacion/tipos-evaluacion-list').then(m => m.TiposEvaluacionList) },
      { path: 'horarios', loadComponent: () => import('./features/horarios/horarios-list').then(m => m.HorariosList) },
      { path: 'sesiones', loadComponent: () => import('./features/sesiones/sesiones-list').then(m => m.SesionesList) },
      { path: 'asistencias', loadComponent: () => import('./features/asistencias/asistencias').then(m => m.Asistencias) },
      { path: 'evaluaciones', loadComponent: () => import('./features/evaluaciones/evaluaciones-list').then(m => m.EvaluacionesList) },
      { path: 'notas/:id', loadComponent: () => import('./features/notas/notas-registro').then(m => m.NotasRegistro) },
      { path: 'usuarios', loadComponent: () => import('./features/usuarios/usuarios-list').then(m => m.UsuariosList), canActivate: [adminGuard] },
      { path: 'roles', loadComponent: () => import('./features/roles/roles-list').then(m => m.RolesList), canActivate: [adminGuard] },
      { path: 'notificaciones', loadComponent: () => import('./features/notificaciones/notificaciones-list').then(m => m.NotificacionesList) },
      { path: 'conceptos-pago', loadComponent: () => import('./features/conceptos-pago/conceptos-pago-list').then(m => m.ConceptosPagoList) },
      { path: 'cronograma-pagos', loadComponent: () => import('./features/cronograma-pagos/cronogramas-pago-list').then(m => m.CronogramasPagoList) },
      { path: 'pagos', loadComponent: () => import('./features/pagos/pagos-list').then(m => m.PagosList) },
      { path: 'padres', loadComponent: () => import('./features/padres/padres-list').then(m => m.PadresList) },
      { path: 'documentos', loadComponent: () => import('./features/documentos/documentos-list').then(m => m.DocumentosList) },
      { path: 'alertas-faltas', loadComponent: () => import('./features/alertas-faltas/alertas-faltas-list').then(m => m.AlertasFaltasList) },
      { path: 'estados-alumno', loadComponent: () => import('./features/estados-alumno/estados-alumno-list').then(m => m.EstadosAlumnoList) },
      { path: 'transiciones-estado', loadComponent: () => import('./features/transiciones-estado/transiciones-estado-list').then(m => m.TransicionesEstadoList), canActivate: [adminGuard] },
      { path: 'reportes', loadComponent: () => import('./features/reportes/reportes').then(m => m.Reportes) },
      { path: 'stripe-payment', loadComponent: () => import('./stripe/stripe-payment').then(m => m.StripePayment) },
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
