import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ConceptoPago, Grado } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-conceptos-pago-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Conceptos de Pago</h1><button class="btn btn-primary" (click)="nuevo()">Nuevo Concepto</button></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Nombre</th><th>Monto Base</th><th>Periodicidad</th><th>Grado</th><th></th></tr></thead>
        <tbody>
          @for (c of paginaActual; track c.id) {
            <tr>
              <td>{{ c.id }}</td><td>{{ c.nombre }}</td><td>S/ {{ (c.montoBase ?? 0).toFixed(2) }}</td>
              <td>{{ c.periodicidad ?? '—' }}</td><td>{{ gradoLabel(c.gradoId) }}</td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(c)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(c)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="6" class="empty">No hay conceptos de pago</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Concepto' : 'Nuevo Concepto'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Nombre</label><input [(ngModel)]="form.nombre" name="nombre" required></div>
        <div class="row">
          <div class="field"><label>Monto Base (S/)</label><input type="number" step="0.01" [(ngModel)]="form.montoBase" name="montoBase" required></div>
          <div class="field"><label>Periodicidad</label>
            <select [(ngModel)]="form.periodicidad" name="periodicidad">
              <option value="">—</option><option value="MENSUAL">Mensual</option><option value="BIMESTRAL">Bimestral</option><option value="TRIMESTRAL">Trimestral</option><option value="ANUAL">Anual</option><option value="UNICO">Único</option>
            </select>
          </div>
        </div>
        <div class="field"><label>Grado (opcional)</label>
          <select [(ngModel)]="form.gradoId" name="gradoId">
            <option value="">Todos los grados</option>
            @for (g of grados; track g.id) { <option [value]="g.id">{{ g.nombre }} ({{ g.nivel }})</option> }
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
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: .03em; border-bottom: 2px solid #e9ecef; }
    td { padding: .75rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn-sm { padding: .25rem .625rem; border: 1px solid #dadce0; border-radius: 4px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .row { display: flex; gap: .75rem; }
    .row > * { flex: 1; }
    .field { display: flex; flex-direction: column; gap: .25rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input, select { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class ConceptosPagoList implements OnInit {
  private api = inject(ApiService);
  list: ConceptoPago[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  grados: Grado[] = [];
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<ConceptoPago> = {};
  private editId?: number;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }

  ngOnInit() { this.cargar(); this.api.get<Grado[]>('/grados').subscribe(r => this.grados = r); }
  cargar() { this.loading = true; this.api.get<ConceptoPago[]>('/conceptos-pago').subscribe(r => { this.list = r; this.loading = false; }); }
  gradoLabel(id?: number) { if (!id) return '—'; const g = this.grados.find(x => x.id === id); return g ? g.nombre : '—'; }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = {}; this.showForm.set(true); }
  editar(c: ConceptoPago) { this.editId = c.id; this.editando.set(true); this.form = { ...c }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<ConceptoPago>('/conceptos-pago', this.editId, this.form) : this.api.post<ConceptoPago>('/conceptos-pago', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(c: ConceptoPago) { if (confirm(`¿Eliminar "${c.nombre}"?`)) this.api.delete('/conceptos-pago', c.id!).subscribe({ next: () => this.cargar() }); }
}
