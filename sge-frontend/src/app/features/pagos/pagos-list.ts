import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Pago, Alumno, CronogramaPago } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-pagos-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Pagos</h1><button class="btn btn-primary" (click)="nuevo()" [disabled]="!alumnoId">Nuevo Pago</button></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="card">
      <div class="field"><label>Alumno</label>
        <select [(ngModel)]="alumnoId" (change)="cargar()">
          <option value="">Seleccione alumno...</option>
          @for (a of alumnos; track a.id) { <option [value]="a.id">{{ a.nombres }} {{ a.apellidos }} ({{ a.codigoEstudiante }})</option> }
        </select>
      </div>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Alumno</th><th>Cronograma</th><th>Monto</th><th>Método</th><th>Referencia</th><th>Fecha</th><th></th></tr></thead>
        <tbody>
          @for (p of paginaActual; track p.id) {
            <tr>
              <td>{{ p.id }}</td><td>{{ p.alumnoNombre ?? '—' }}</td><td>#{{ p.cronogramaPagoId }}</td>
              <td>S/ {{ (p.monto ?? 0).toFixed(2) }}</td><td>{{ p.metodo || '—' }}</td><td>{{ p.referencia || '—' }}</td>
              <td>{{ p.fechaPago }}</td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(p)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(p)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="8" class="empty">{{ alumnoId ? 'Sin pagos registrados' : 'Seleccione un alumno' }}</td></tr> }
        </tbody>
      </table>
    </div>
    @if (totalPaginas > 1) {
      <div class="pagination">
        <button class="btn-sm" [disabled]="pagina === 0" (click)="pagina=pagina-1">Anterior</button>
        <span>Pág. {{ pagina + 1 }} de {{ totalPaginas }}</span>
        <button class="btn-sm" [disabled]="pagina >= totalPaginas-1" (click)="pagina=pagina+1">Siguiente</button>
      </div>
    }
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Pago' : 'Nuevo Pago'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Cronograma</label>
          <select [(ngModel)]="form.cronogramaPagoId" name="cronogramaPagoId" required>
            <option value="">Seleccione...</option>
            @for (c of cronogramas; track c.id) { <option [value]="c.id">{{ c.conceptoNombre }} — {{ c.fechaVencimiento }} (S/ {{ (c.monto ?? 0).toFixed(2) }})</option> }
          </select>
        </div>
        <div class="row">
          <div class="field"><label>Monto (S/)</label><input type="number" step="0.01" [(ngModel)]="form.monto" name="monto" required></div>
          <div class="field"><label>Fecha</label><input type="date" [(ngModel)]="form.fechaPago" name="fechaPago"></div>
        </div>
        <div class="row">
          <div class="field"><label>Método</label>
            <select [(ngModel)]="form.metodo" name="metodo">
              <option value="">—</option><option value="EFECTIVO">Efectivo</option><option value="TRANSFERENCIA">Transferencia</option>
              <option value="TARJETA">Tarjeta</option><option value="STRIPE">Stripe</option><option value="OTRO">Otro</option>
            </select>
          </div>
          <div class="field"><label>Referencia</label><input [(ngModel)]="form.referencia" name="referencia"></div>
        </div>
        <div class="form-actions">
          <button type="button" class="btn btn-cancel" (click)="cerrarForm()">Cancelar</button>
          <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Guardando...' : 'Guardar' }}</button>
        </div>
      </form>
    </sge-modal>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; }
    .field { display: flex; flex-direction: column; gap: .25rem; flex: 1; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select, input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: .03em; border-bottom: 2px solid #e9ecef; }
    td { padding: .75rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; white-space: nowrap; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    .btn-sm { padding: .25rem .625rem; border: 1px solid #dadce0; border-radius: 4px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .row { display: flex; gap: .75rem; }
    .row > * { flex: 1; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class PagosList implements OnInit {
  private api = inject(ApiService);
  list: Pago[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  alumnos: Alumno[] = [];
  cronogramas: CronogramaPago[] = [];
  alumnoId = 0;
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<Pago> = {};
  private editId?: number;
  ngOnInit() {
    this.api.get<Alumno[]>('/alumnos').subscribe(r => this.alumnos = r);
  }
  cargar() {
    if (this.alumnoId) {
      this.loading = true;
      this.pagina = 0;
      this.api.get<Pago[]>('/pagos/alumno/' + this.alumnoId).subscribe(r => { this.list = r; this.loading = false; });
      this.api.get<CronogramaPago[]>('/cronograma-pagos/alumno/' + this.alumnoId).subscribe(r => this.cronogramas = r);
    }
  }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = { alumnoId: this.alumnoId }; this.showForm.set(true); }
  editar(p: Pago) { this.editId = p.id; this.editando.set(true); this.form = { ...p }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<Pago>('/pagos', this.editId, this.form) : this.api.post<Pago>('/pagos', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(p: Pago) { if (confirm('¿Eliminar pago?')) this.api.delete('/pagos', p.id!).subscribe({ next: () => this.cargar() }); }
}
