import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { CronogramaPago, Alumno, ConceptoPago, PeriodoAcademico } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-cronogramas-pago-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Cronograma de Pagos</h1><button class="btn btn-primary" (click)="nuevo()" [disabled]="!alumnoId">Nuevo</button></div>
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
        <thead><tr><th>ID</th><th>Alumno</th><th>Concepto</th><th>Vencimiento</th><th>Monto</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          @for (c of paginaActual; track c.id) {
            <tr>
              <td>{{ c.id }}</td><td>{{ c.alumnoNombre ?? '—' }}</td><td>{{ c.conceptoNombre ?? '—' }}</td>
              <td>{{ c.fechaVencimiento }}</td><td>S/ {{ (c.monto ?? 0).toFixed(2) }}</td>
              <td><span class="badge" [class.badge-ok]="c.estado==='PAGADO'" [class.badge-warn]="c.estado==='VENCIDO'">{{ c.estado }}</span></td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(c)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(c)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">{{ alumnoId ? 'Sin cronogramas' : 'Seleccione un alumno' }}</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Cronograma' : 'Nuevo Cronograma'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Concepto</label>
          <select [(ngModel)]="form.conceptoPagoId" name="conceptoPagoId" required>
            <option value="">Seleccione...</option>
            @for (c of conceptos; track c.id) { <option [value]="c.id">{{ c.nombre }} (S/ {{ (c.montoBase ?? 0).toFixed(2) }})</option> }
          </select>
        </div>
        <div class="field"><label>Período</label>
          <select [(ngModel)]="form.periodoId" name="periodoId" required>
            <option value="">Seleccione...</option>
            @for (p of periodos; track p.id) { <option [value]="p.id">{{ p.nombre }} ({{ p.codigo }})</option> }
          </select>
        </div>
        <div class="row">
          <div class="field"><label>Monto (S/)</label><input type="number" step="0.01" [(ngModel)]="form.monto" name="monto" required></div>
          <div class="field"><label>Vencimiento</label><input type="date" [(ngModel)]="form.fechaVencimiento" name="fechaVencimiento" required></div>
        </div>
        <div class="field"><label>Estado</label>
          <select [(ngModel)]="form.estado" name="estado" required>
            <option value="PENDIENTE">Pendiente</option><option value="PAGADO">Pagado</option><option value="VENCIDO">Vencido</option>
          </select>
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
    .badge { display: inline-block; padding: .1875rem .5rem; border-radius: 99px; font-size: .75rem; font-weight: 600; }
    .badge-ok { background: #d4edda; color: #155724; }
    .badge-warn { background: #fff3cd; color: #856404; }
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
export class CronogramasPagoList implements OnInit {
  private api = inject(ApiService);
  list: CronogramaPago[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  alumnos: Alumno[] = [];
  conceptos: ConceptoPago[] = [];
  periodos: PeriodoAcademico[] = [];
  alumnoId = 0;
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<CronogramaPago> = { estado: 'PENDIENTE' };
  private editId?: number;
  ngOnInit() {
    this.api.get<Alumno[]>('/alumnos').subscribe(r => this.alumnos = r);
    this.api.get<ConceptoPago[]>('/conceptos-pago').subscribe(r => this.conceptos = r);
    this.api.get<PeriodoAcademico[]>('/periodos').subscribe(r => this.periodos = r);
  }
  cargar() {
    if (this.alumnoId) {
      this.loading = true;
      this.pagina = 0;
      this.api.get<CronogramaPago[]>('/cronograma-pagos/alumno/' + this.alumnoId).subscribe(r => { this.list = r; this.loading = false; });
    }
  }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = { estado: 'PENDIENTE', alumnoId: this.alumnoId }; this.showForm.set(true); }
  editar(c: CronogramaPago) { this.editId = c.id; this.editando.set(true); this.form = { ...c }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<CronogramaPago>('/cronograma-pagos', this.editId, this.form) : this.api.post<CronogramaPago>('/cronograma-pagos', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(c: CronogramaPago) { if (confirm('¿Eliminar cronograma?')) this.api.delete('/cronograma-pagos', c.id!).subscribe({ next: () => this.cargar() }); }
}
