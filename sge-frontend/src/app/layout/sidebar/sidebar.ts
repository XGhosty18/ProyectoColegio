import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'sge-sidebar',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="sidebar">
      <a routerLink="/dashboard" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Dashboard</a>
      @if (auth.isAdmin()) {
        <div class="section">Administración</div>
        <a routerLink="/periodos" routerLinkActive="active">Periodos</a>
        <a routerLink="/usuarios" routerLinkActive="active">Usuarios</a>
        <a routerLink="/roles" routerLinkActive="active">Roles</a>
      }
      <a routerLink="/notificaciones" routerLinkActive="active">Notificaciones</a>
      @if (auth.isAdmin()) {
        <a routerLink="/padres" routerLinkActive="active">Padres</a>
        <a routerLink="/transiciones-estado" routerLinkActive="active">Transiciones Estado</a>
      }
      <div class="section">Académico</div>
      <a routerLink="/grados" routerLinkActive="active">Grados</a>
      <a routerLink="/docentes" routerLinkActive="active">Docentes</a>
      <a routerLink="/alumnos" routerLinkActive="active">Alumnos</a>
      <a routerLink="/cursos" routerLinkActive="active">Cursos</a>
      <a routerLink="/materias" routerLinkActive="active">Materias</a>
      <a routerLink="/aulas" routerLinkActive="active">Aulas</a>
      <a routerLink="/horarios" routerLinkActive="active">Horarios</a>
      <a routerLink="/evaluaciones" routerLinkActive="active">Evaluaciones</a>
      <a routerLink="/asistencias" routerLinkActive="active">Asistencias</a>
      <div class="section">Pagos</div>
      <a routerLink="/conceptos-pago" routerLinkActive="active">Conceptos</a>
      <a routerLink="/cronograma-pagos" routerLinkActive="active">Cronograma</a>
      <a routerLink="/pagos" routerLinkActive="active">Pagos</a>
      <div class="section">Reportes</div>
      <a routerLink="/estados-alumno" routerLinkActive="active">Estados Alumno</a>
      <a routerLink="/documentos" routerLinkActive="active">Documentos</a>
      <a routerLink="/alertas-faltas" routerLinkActive="active">Alertas Faltas</a>
      <a routerLink="/reportes" routerLinkActive="active">Reporte Notas</a>
      <div class="section">Pagos Online</div>
      <a routerLink="/stripe-payment" routerLinkActive="active">Pago Stripe</a>
    </nav>
  `,
  styles: [`
    .sidebar { width: 220px; background: #2c3e50; color: #ecf0f1; display: flex; flex-direction: column; padding: 1rem 0; overflow-y: auto; }
    .sidebar a { padding: .625rem 1.25rem; color: #bdc3c7; text-decoration: none; font-size: .875rem; transition: all .15s; }
    .sidebar a:hover { background: rgba(255,255,255,.08); color: #fff; }
    .sidebar a.active { background: rgba(26,115,232,.3); color: #fff; border-left: 3px solid #1a73e8; }
    .section { padding: .75rem 1.25rem .25rem; font-size: .6875rem; text-transform: uppercase; letter-spacing: .05em; color: #7f8c8d; font-weight: 600; }
  `]
})
export class Sidebar {
  protected auth = inject(AuthService);
}
