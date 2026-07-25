import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { Nota, Evaluacion, Alumno } from '../../core/models/types';

@Component({
  selector: 'sge-notas-registro',
  imports: [FormsModule],
  template: `
    <div class="page-header">
      <h1>Registro de Notas</h1>
      <a routerLink="/evaluaciones" class="btn btn-cancel">← Volver</a>
    </div>
    @if (evaluacion) {
      <div class="card">
        <p><strong>{{ evaluacion.nombre }}</strong> — {{ evaluacion.tipoEvaluacionNombre }} — {{ evaluacion.fecha }}</p>
        <p class="sub">{{ evaluacion.cursoNombre }}</p>
      </div>
    }
    @if (alumnos.length > 0) {
      <div class="table-wrap">
        <table>
          <thead><tr><th>#</th><th>Alumno</th><th>Nota (0-20)</th><th>Observación</th></tr></thead>
          <tbody>
            @for (a of alumnos; track a.alumnoId; let i = $index) {
              <tr>
                <td>{{ i + 1 }}</td>
                <td>{{ a.nombre }}</td>
                <td><input type="number" step="0.01" min="0" max="20" [(ngModel)]="a.valor" [ngModelOptions]="{standalone:true}" style="width:100px"></td>
                <td><input [(ngModel)]="a.observacion" [ngModelOptions]="{standalone:true}" style="width:100%"></td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      <button class="btn btn-primary" style="margin-top:1rem" (click)="guardar()" [disabled]="saving">{{ saving ? 'Guardando...' : 'Guardar Notas' }}</button>
    } @else {
      <div class="empty-state">Cargando alumnos...</div>
    }
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; }
    p { margin: 0; font-size: 1rem; color: #333; }
    .sub { font-size: .875rem; color: #666; margin-top: .25rem; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; border-bottom: 2px solid #e9ecef; }
    td { padding: .5rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .empty-state { text-align: center; color: #999; padding: 3rem; background: #fff; border-radius: 8px; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; text-decoration: none; display: inline-block; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    input { padding: .375rem .5rem; border: 1px solid #dadce0; border-radius: 4px; font-size: .875rem; }
  `]
})
export class NotasRegistro implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  evaluacionId = 0;
  evaluacion?: Evaluacion;
  alumnos: { alumnoId: number; nombre: string; valor: number; observacion?: string; existenteId?: number }[] = [];
  saving = false;

  ngOnInit() {
    this.evaluacionId = Number(this.route.snapshot.params['id']);
    this.api.get<Evaluacion>('/evaluaciones/' + this.evaluacionId).subscribe(e => {
      this.evaluacion = e;
      this.api.get<Nota[]>('/notas/evaluacion/' + this.evaluacionId).subscribe(existentes => {
        this.api.get<Alumno[]>('/alumnos').subscribe(alumnos => {
          this.alumnos = alumnos.map(a => {
            const existente = existentes.find(n => n.alumnoId === a.id);
            return { alumnoId: a.id!, nombre: a.nombres + ' ' + a.apellidos, valor: existente?.valor ?? 0, observacion: existente?.observacion, existenteId: existente?.id };
          });
        });
      });
    });
  }

  guardar() {
    this.saving = true;
    const nuevas = this.alumnos.filter(a => !a.existenteId && a.valor > 0).map(a => ({ evaluacionId: this.evaluacionId, alumnoId: a.alumnoId, valor: a.valor, observacion: a.observacion || null }));
    const updates = this.alumnos.filter(a => a.existenteId).map(a => ({ id: a.existenteId!, body: { evaluacionId: this.evaluacionId, alumnoId: a.alumnoId, valor: a.valor, observacion: a.observacion || null } }));
    let completed = 0; const total = nuevas.length + updates.length;
    if (total === 0) { this.saving = false; return; }
    if (nuevas.length > 0) {
      this.api.post<Nota>('/notas/masivo', nuevas).subscribe({
        next: () => { completed += nuevas.length; if (completed === total) { this.saving = false; alert('Notas guardadas'); } },
        error: () => { this.saving = false; alert('Error al guardar'); }
      });
    }
    updates.forEach(u => this.api.put<Nota>('/notas', u.id!, u.body).subscribe({
      next: () => { completed++; if (completed === total) { this.saving = false; alert('Notas guardadas'); } },
      error: () => { this.saving = false; alert('Error al actualizar'); }
    }));
  }
}
