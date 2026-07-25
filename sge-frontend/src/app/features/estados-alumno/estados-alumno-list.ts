import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Alumno, EstadoAlumno, HistorialEstado, CambioEstadoRequest } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-estados-alumno-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Estados de Alumno</h1></div>
    <div class="card">
      <div class="row-fields">
        <div class="field"><label>Alumno</label>
          <select [(ngModel)]="alumnoId" (change)="cargarHistorial()">
            <option value="">Seleccione alumno...</option>
            @for (a of alumnos; track a.id) { <option [value]="a.id">{{ a.nombres }} {{ a.apellidos }} ({{ a.codigoEstudiante }})</option> }
          </select>
        </div>
        <button class="btn btn-primary" [disabled]="!alumnoId" (click)="mostrarTransicion()">Cambiar Estado</button>
      </div>
    </div>
    @if (alumnoActual) {
      <div class="current-state">Estado actual: <strong>{{ alumnoActual.estadoActualNombre || alumnoActual.estadoActualCodigo || '—' }}</strong></div>
    }
    <div class="table-wrap">
      <table>
        <thead><tr><th>Fecha</th><th>Desde</th><th>Hacia</th><th>Motivo</th><th>Registrado por</th></tr></thead>
        <tbody>
          @for (h of historial; track h.id) {
            <tr>
              <td>{{ formatDate(h.fechaCambio) }}</td>
              <td>{{ h.estadoOrigenCodigo || '—' }}</td>
              <td>{{ h.estadoDestinoNombre || h.estadoDestinoCodigo }}</td>
              <td>{{ h.motivo || '—' }}</td>
              <td>{{ h.registradoPor || '—' }}</td>
            </tr>
          } @empty { <tr><td colspan="5" class="empty">{{ alumnoId ? 'Sin historial de estados' : 'Seleccione un alumno' }}</td></tr> }
        </tbody>
      </table>
    </div>
    <sge-modal [open]="showForm()" title="Cambiar Estado" (close)="cerrarForm()">
      <form (ngSubmit)="hacerTransicion()" class="form">
        <div class="field"><label>Nuevo Estado</label>
          <select [(ngModel)]="transicionForm.estadoCodigo" name="estadoCodigo" required>
            <option value="">Seleccione...</option>
            @for (e of estados; track e.id) { <option [value]="e.codigo">{{ e.nombre }} ({{ e.codigo }})</option> }
          </select>
        </div>
        <div class="field"><label>Motivo</label><textarea [(ngModel)]="transicionForm.motivo" name="motivo" rows="3"></textarea></div>
        <div class="field"><label>Documento referencia</label><input [(ngModel)]="transicionForm.referenciaDocumento" name="refDoc"></div>
        <div class="form-actions">
          <button type="button" class="btn btn-cancel" (click)="cerrarForm()">Cancelar</button>
          <button type="submit" class="btn btn-primary" [disabled]="saving || !transicionForm.estadoCodigo">{{ saving ? 'Guardando...' : 'Cambiar Estado' }}</button>
        </div>
      </form>
    </sge-modal>
  `,
  styles: [`
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; }
    .row-fields { display: flex; gap: .75rem; align-items: flex-end; }
    .field { display: flex; flex-direction: column; gap: .25rem; flex: 1; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select, input, textarea { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; font-family: inherit; }
    textarea { resize: vertical; }
    .current-state { margin-bottom: 1rem; font-size: .9375rem; color: #333; padding: .75rem 1rem; background: #e8f0fe; border-radius: 8px; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: .03em; border-bottom: 2px solid #e9ecef; }
    td { padding: .75rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; white-space: nowrap; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
  `]
})
export class EstadosAlumnoList implements OnInit {
  private api = inject(ApiService);
  alumnos: Alumno[] = [];
  estados: EstadoAlumno[] = [];
  historial: HistorialEstado[] = [];
  alumnoId = 0;
  alumnoActual?: Alumno;
  showForm = signal(false);
  saving = false;
  transicionForm: CambioEstadoRequest = { alumnoId: 0, estadoCodigo: '', motivo: '' };
  ngOnInit() {
    this.api.get<Alumno[]>('/alumnos').subscribe(r => this.alumnos = r);
    this.api.get<EstadoAlumno[]>('/estados-alumno').subscribe(r => this.estados = r);
  }
  cargarHistorial() {
    if (!this.alumnoId) { this.historial = []; this.alumnoActual = undefined; return; }
    this.alumnoActual = this.alumnos.find(a => a.id === this.alumnoId);
    this.api.get<HistorialEstado[]>('/estados-alumno/' + this.alumnoId + '/historial').subscribe(r => this.historial = r);
  }
  mostrarTransicion() { this.transicionForm = { alumnoId: this.alumnoId, estadoCodigo: '', motivo: '' }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  hacerTransicion() {
    this.saving = true;
    this.api.post<any>('/estados-alumno/transicion', this.transicionForm).subscribe({
      next: () => { this.cargarHistorial(); this.cerrarForm(); this.saving = false; },
      error: () => { alert('Error al cambiar estado'); this.saving = false; }
    });
  }
  formatDate(iso?: string): string {
    if (!iso) return '';
    return new Date(iso).toLocaleDateString('es-PE', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }
}
