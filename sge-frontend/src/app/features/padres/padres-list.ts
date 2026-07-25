import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Padre } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-padres-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Padres de Familia</h1><button class="btn btn-primary" (click)="nuevo()">Nuevo Padre</button></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Nombres</th><th>Apellidos</th><th>DNI</th><th>Teléfono</th><th>Parentesco</th><th>Titular</th><th></th></tr></thead>
        <tbody>
          @for (p of paginaActual; track p.id) {
            <tr>
              <td>{{ p.id }}</td><td>{{ p.nombres }}</td><td>{{ p.apellidos }}</td><td>{{ p.dni }}</td>
              <td>{{ p.telefono || '—' }}</td><td>{{ p.parentesco || '—' }}</td><td>{{ p.esTitular ? '✓' : '—' }}</td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(p)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(p)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="8" class="empty">No hay padres registrados</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Padre' : 'Nuevo Padre'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="row">
          <div class="field"><label>Nombres</label><input [(ngModel)]="form.nombres" name="nombres" required></div>
          <div class="field"><label>Apellidos</label><input [(ngModel)]="form.apellidos" name="apellidos" required></div>
        </div>
        <div class="row">
          <div class="field"><label>DNI</label><input [(ngModel)]="form.dni" name="dni" required maxlength="8"></div>
          <div class="field"><label>Teléfono</label><input [(ngModel)]="form.telefono" name="telefono"></div>
        </div>
        <div class="row">
          <div class="field"><label>Parentesco</label>
            <select [(ngModel)]="form.parentesco" name="parentesco">
              <option value="">—</option><option value="PADRE">Padre</option><option value="MADRE">Madre</option><option value="TUTOR">Tutor</option><option value="OTRO">Otro</option>
            </select>
          </div>
          <div class="field"><label>Género</label>
            <select [(ngModel)]="form.genero" name="genero">
              <option value="">—</option><option value="M">Masculino</option><option value="F">Femenino</option>
            </select>
          </div>
        </div>
        <div class="row">
          <div class="field"><label>Fecha Nac.</label><input type="date" [(ngModel)]="form.fechaNac" name="fechaNac"></div>
          <div class="field"><label>Dirección</label><input [(ngModel)]="form.direccion" name="direccion"></div>
        </div>
        <div class="field checkbox"><label><input type="checkbox" [(ngModel)]="form.esTitular" name="esTitular"> Es titular</label></div>
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
    .field.checkbox { flex-direction: row; align-items: center; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select, input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class PadresList implements OnInit {
  private api = inject(ApiService);
  list: Padre[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<Padre> = {};
  private editId?: number;
  ngOnInit() { this.cargar(); }
  cargar() {
    this.loading = true;
    this.pagina = 0;
    this.api.get<Padre[]>('/padres').subscribe(r => { this.list = r; this.loading = false; });
  }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = {}; this.showForm.set(true); }
  editar(p: Padre) { this.editId = p.id; this.editando.set(true); this.form = { ...p }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<Padre>('/padres', this.editId, this.form) : this.api.post<Padre>('/padres', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(p: Padre) { if (confirm('¿Eliminar padre?')) this.api.delete('/padres', p.id!).subscribe({ next: () => this.cargar() }); }
}
