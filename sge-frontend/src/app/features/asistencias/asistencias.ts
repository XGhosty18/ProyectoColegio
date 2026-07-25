import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Asistencia, SesionClase, Curso, Alumno } from '../../core/models/types';

@Component({
  selector: 'sge-asistencias',
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Asistencias</h1></div>
    <div class="card">
      <div class="field"><label>Curso</label>
        <select [(ngModel)]="cursoId" (change)="cargarSesiones()">
          <option value="">Seleccione curso...</option>
          @for (c of cursos; track c.id) { <option [value]="c.id">{{ c.gradoNombre }} - {{ c.materiaNombre }}{{ c.seccionNombre ? ' ('+c.seccionNombre+')' : '' }}</option> }
        </select>
      </div>
      <div class="field"><label>Sesión</label>
        <select [(ngModel)]="sesionId" (change)="cargarAsistencias()" [disabled]="!cursoId">
          <option value="">Seleccione sesión...</option>
          @for (s of sesiones; track s.id) { <option [value]="s.id">{{ s.fecha }} {{ s.tema ? '— '+s.tema : '' }}</option> }
        </select>
      </div>
    </div>
    @if (alumnos.length > 0) {
      <div class="table-wrap">
        <table>
          <thead><tr><th>#</th><th>Alumno</th><th>Asistencia</th><th>Min. Tardanza</th><th>Observación</th></tr></thead>
          <tbody>
            @for (a of alumnos; track a.alumnoId; let i = $index) {
              <tr>
                <td>{{ i + 1 }}</td>
                <td>{{ a.nombre }}</td>
                <td>
                  <select [(ngModel)]="a.tipoAsistencia" [ngModelOptions]="{standalone:true}">
                    <option value="PRESENTE">Presente</option><option value="FALTA">Falta</option>
                    <option value="TARDANZA">Tardanza</option><option value="JUSTIFICADO">Justificado</option><option value="LICENCIA">Licencia</option>
                  </select>
                </td>
                <td><input type="number" [(ngModel)]="a.minutosTardanza" [ngModelOptions]="{standalone:true}" style="width:80px"></td>
                <td><input [(ngModel)]="a.observacion" [ngModelOptions]="{standalone:true}" style="width:100%"></td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      <button class="btn btn-primary" style="margin-top:1rem" (click)="guardar()" [disabled]="saving">{{ saving ? 'Guardando...' : 'Guardar Asistencias' }}</button>
    }
  `,
  styles: [`
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; display: flex; gap: 1rem; align-items: end; }
    .field { display: flex; flex-direction: column; gap: .25rem; flex: 1; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select, input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; border-bottom: 2px solid #e9ecef; }
    td { padding: .5rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-primary:hover { background: #1557b0; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
  `]
})
export class Asistencias implements OnInit {
  private api = inject(ApiService);
  cursos: Curso[] = [];
  sesiones: SesionClase[] = [];
  cursoId = 0;
  sesionId = 0;
  saving = false;
  alumnos: { alumnoId: number; nombre: string; tipoAsistencia: string; minutosTardanza?: number; observacion?: string; existenteId?: number }[] = [];

  ngOnInit() { this.api.get<Curso[]>('/cursos').subscribe(r => this.cursos = r); }
  cargarSesiones() { this.sesionId = 0; this.alumnos = []; if (this.cursoId) this.api.get<SesionClase[]>('/sesiones/curso/' + this.cursoId).subscribe(r => this.sesiones = r); }
  cargarAsistencias() {
    if (!this.sesionId) { this.alumnos = []; return; }
    this.api.get<Asistencia[]>('/asistencias/sesion/' + this.sesionId).subscribe(existentes => {
      this.api.get<Alumno[]>('/alumnos').subscribe(alumnos => {
        this.alumnos = alumnos.map(a => {
          const existente = existentes.find(e => e.alumnoId === a.id);
          return { alumnoId: a.id!, nombre: a.nombres + ' ' + a.apellidos, tipoAsistencia: existente?.tipoAsistencia ?? 'PRESENTE', minutosTardanza: existente?.minutosTardanza, observacion: existente?.observacion, existenteId: existente?.id };
        });
      }, () => this.alumnos = []);
    });
  }
  guardar() {
    this.saving = true;
    const requests = this.alumnos.filter(a => !a.existenteId).map(a => ({ sesionId: this.sesionId, alumnoId: a.alumnoId, tipoAsistencia: a.tipoAsistencia, minutosTardanza: a.minutosTardanza || null, observacion: a.observacion || null }));
    const updates = this.alumnos.filter(a => a.existenteId).map(a => ({ id: a.existenteId!, body: { sesionId: this.sesionId, alumnoId: a.alumnoId, tipoAsistencia: a.tipoAsistencia, minutosTardanza: a.minutosTardanza || null, observacion: a.observacion || null } }));
    let completed = 0; const total = requests.length + updates.length;
    if (total === 0) { this.saving = false; return; }
    if (requests.length > 0) {
      this.api.post<Asistencia>('/asistencias/masivo', requests).subscribe({ next: () => { completed += requests.length; if (completed === total) { this.saving = false; alert('Asistencias guardadas'); } }, error: () => { this.saving = false; alert('Error al guardar masivo'); } });
    }
    updates.forEach(u => this.api.put<Asistencia>('/asistencias', u.id!, u.body).subscribe({ next: () => { completed++; if (completed === total) { this.saving = false; alert('Asistencias guardadas'); } }, error: () => { this.saving = false; alert('Error al actualizar'); } }));
  }
}
