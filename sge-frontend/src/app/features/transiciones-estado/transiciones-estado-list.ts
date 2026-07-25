import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { TransicionEstado, EstadoAlumno } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-transiciones-estado-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Transiciones de Estado</h1><button class="btn btn-primary" (click)="nuevo()">Nueva Transición</button></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Desde</th><th>Hacia</th><th>Código</th><th>Automática</th><th>Requiere Admin</th><th>Notifica Padre</th><th></th></tr></thead>
        <tbody>
          @for (t of paginaActual; track t.id) {
            <tr>
              <td>{{ t.id }}</td><td>{{ t.estadoOrigenNombre || '—' }}</td><td>{{ t.estadoDestinoNombre || '—' }}</td>
              <td><code>{{ t.codigoGatillante }}</code></td>
              <td>{{ t.esAutomatica ? '✓' : '—' }}</td>
              <td>{{ t.requiereAdmin ? '✓' : '—' }}</td>
              <td>{{ t.notificaPadre ? '✓' : '—' }}</td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(t)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(t)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="8" class="empty">No hay transiciones registradas</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Transición' : 'Nueva Transición'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="row">
          <div class="field"><label>Estado Origen</label>
            <select [(ngModel)]="form.estadoOrigenId" name="estadoOrigenId" required>
              <option value="">Seleccione...</option>
              @for (e of estados; track e.id) { <option [value]="e.id">{{ e.nombre }} ({{ e.codigo }})</option> }
            </select>
          </div>
          <div class="field"><label>Estado Destino</label>
            <select [(ngModel)]="form.estadoDestinoId" name="estadoDestinoId" required>
              <option value="">Seleccione...</option>
              @for (e of estados; track e.id) { <option [value]="e.id">{{ e.nombre }} ({{ e.codigo }})</option> }
            </select>
          </div>
        </div>
        <div class="field"><label>Código Gatillante</label><input [(ngModel)]="form.codigoGatillante" name="codigoGatillante" required></div>
        <div class="row">
          <div class="field checkbox"><label><input type="checkbox" [(ngModel)]="form.esAutomatica" name="esAutomatica"> Automática</label></div>
          <div class="field checkbox"><label><input type="checkbox" [(ngModel)]="form.requiereAdmin" name="requiereAdmin"> Requiere Admin</label></div>
          <div class="field checkbox"><label><input type="checkbox" [(ngModel)]="form.notificaPadre" name="notificaPadre"> Notifica Padre</label></div>
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
    td code { background: #f1f3f4; padding: .125rem .375rem; border-radius: 3px; font-size: .8125rem; }
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
    .field { display: flex; flex-direction: column; gap: .25rem; flex: 1; }
    .field.checkbox { flex-direction: row; align-items: center; gap: .5rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select, input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class TransicionesEstadoList implements OnInit {
  private api = inject(ApiService);
  list: TransicionEstado[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  estados: EstadoAlumno[] = [];
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<TransicionEstado> = {};
  private editId?: number;
  ngOnInit() {
    this.cargar();
    this.api.get<EstadoAlumno[]>('/estados-alumno').subscribe(r => this.estados = r);
  }
  cargar() {
    this.loading = true;
    this.pagina = 0;
    this.api.get<TransicionEstado[]>('/transiciones-estado').subscribe(r => { this.list = r; this.loading = false; });
  }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = {}; this.showForm.set(true); }
  editar(t: TransicionEstado) { this.editId = t.id; this.editando.set(true); this.form = { ...t }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<TransicionEstado>('/transiciones-estado', this.editId, this.form) : this.api.post<TransicionEstado>('/transiciones-estado', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(t: TransicionEstado) { if (confirm('¿Eliminar transición?')) this.api.delete('/transiciones-estado', t.id!).subscribe({ next: () => this.cargar() }); }
}
