import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Rol, Permiso } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-roles-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Roles</h1><button class="btn btn-primary" (click)="nuevo()">Nuevo Rol</button></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Código</th><th>Nombre</th><th>Permisos</th><th></th></tr></thead>
        <tbody>
          @for (r of paginaActual; track r.id) {
            <tr>
              <td>{{ r.id }}</td><td>{{ r.codigo }}</td><td>{{ r.nombre }}</td>
              <td>@for (p of r.permisos; track p) { <span class="badge">{{ p }}</span> }</td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(r)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(r)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="5" class="empty">No hay roles</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Rol' : 'Nuevo Rol'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="row">
          <div class="field"><label>Código</label><input [(ngModel)]="form.codigo" name="codigo" required></div>
          <div class="field"><label>Nombre</label><input [(ngModel)]="form.nombre" name="nombre" required></div>
        </div>
        <div class="field"><label>Permisos</label>
          <div class="checkbox-group">
            @for (p of permisos; track p.id) {
              <label class="checkbox"><input type="checkbox" [value]="p.id" (change)="togglePermiso(p.id!)" [checked]="selectedPermisos.includes(p.id!)"> <strong>{{ p.codigo }}</strong> — {{ p.descripcion }} <span class="mod">{{ p.modulo ? '('+p.modulo+')' : '' }}</span></label>
            } @empty { <p class="empty">No hay permisos</p> }
          </div>
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
    .badge { display: inline-block; padding: .1875rem .5rem; background: #e8f0fe; color: #1a73e8; border-radius: 99px; font-size: .75rem; font-weight: 600; margin: .125rem; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .row { display: flex; gap: .75rem; }
    .row > * { flex: 1; }
    .field { display: flex; flex-direction: column; gap: .25rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .checkbox-group { display: flex; flex-direction: column; gap: .125rem; border: 1px solid #dadce0; border-radius: 6px; padding: .5rem .75rem; max-height: 300px; overflow-y: auto; }
    .checkbox { display: flex; align-items: center; gap: .5rem; font-weight: 400; font-size: .8125rem; cursor: pointer; padding: .125rem 0; }
    .checkbox input { width: auto; }
    .mod { color: #999; font-size: .75rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class RolesList implements OnInit {
  private api = inject(ApiService);
  list: Rol[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  permisos: Permiso[] = [];
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: any = {};
  selectedPermisos: number[] = [];
  private editId?: number;
  ngOnInit() { this.cargar(); this.api.get<Permiso[]>('/permisos').subscribe(r => this.permisos = r); }
  cargar() {
    this.loading = true;
    this.pagina = 0;
    this.api.get<Rol[]>('/roles').subscribe(r => { this.list = r; this.loading = false; });
  }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = {}; this.selectedPermisos = []; this.showForm.set(true); }
  editar(r: Rol) { this.editId = r.id; this.editando.set(true); this.form = { codigo: r.codigo, nombre: r.nombre }; this.selectedPermisos = this.permisos.filter(p => r.permisos.includes(p.codigo)).map(p => p.id!); this.showForm.set(true); }
  togglePermiso(id: number) { const i = this.selectedPermisos.indexOf(id); if (i >= 0) this.selectedPermisos.splice(i, 1); else this.selectedPermisos.push(id); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const body = { codigo: this.form.codigo, nombre: this.form.nombre, permisoIds: this.selectedPermisos.length ? this.selectedPermisos : null };
    const obs = this.editId ? this.api.put<Rol>('/roles', this.editId, body) : this.api.post<Rol>('/roles', body);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(r: Rol) { if (confirm(`¿Eliminar rol "${r.nombre}"?`)) this.api.delete('/roles', r.id!).subscribe({ next: () => this.cargar() }); }
}
