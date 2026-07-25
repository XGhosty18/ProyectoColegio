import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { Usuario, Rol, Permiso } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-usuarios-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Usuarios</h1><button class="btn btn-primary" (click)="nuevo()">Nuevo Usuario</button></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Username</th><th>Email</th><th>Persona</th><th>Roles</th><th>Activo</th><th></th></tr></thead>
        <tbody>
          @for (u of paginaActual; track u.id) {
            <tr>
              <td>{{ u.id }}</td><td>{{ u.username }}</td><td>{{ u.email }}</td>
              <td>{{ u.personaNombre ?? '—' }}</td>
              <td><span class="badge" [class.badge-admin]="u.roles.includes('ADMIN')">{{ u.roles.join(', ') }}</span></td>
              <td><span class="badge" [class.badge-ok]="u.enabled" [class.badge-danger]="!u.enabled">{{ u.enabled ? 'Sí' : 'No' }}</span></td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(u)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(u)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">No hay usuarios</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Usuario' : 'Nuevo Usuario'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="row">
          <div class="field"><label>Username</label><input [(ngModel)]="form.username" name="username" required></div>
          <div class="field"><label>Email</label><input type="email" [(ngModel)]="form.email" name="email" required></div>
        </div>
        <div class="field"><label>Contraseña{{ editando() ? ' (dejar vacío para mantener)' : '' }}</label><input type="password" [(ngModel)]="form.password" name="password" [required]="!editando()"></div>
        <div class="field"><label>Roles</label>
          <div class="checkbox-group">
            @for (r of roles; track r.id) {
              <label class="checkbox"><input type="checkbox" [value]="r.id" (change)="toggleRol(r.id!)" [checked]="selectedRoles.includes(r.id!)"> {{ r.nombre }} ({{ r.codigo }})</label>
            }
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
    .badge { display: inline-block; padding: .1875rem .5rem; border-radius: 99px; font-size: .75rem; font-weight: 600; }
    .badge-ok { background: #d4edda; color: #155724; }
    .badge-danger { background: #f8d7da; color: #721c24; }
    .badge-admin { background: #cce5ff; color: #004085; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .row { display: flex; gap: .75rem; }
    .row > * { flex: 1; }
    .field { display: flex; flex-direction: column; gap: .25rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .checkbox-group { display: flex; flex-direction: column; gap: .25rem; border: 1px solid #dadce0; border-radius: 6px; padding: .5rem .75rem; max-height: 200px; overflow-y: auto; }
    .checkbox { display: flex; align-items: center; gap: .5rem; font-weight: 400; font-size: .875rem; cursor: pointer; }
    .checkbox input { width: auto; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class UsuariosList implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  list: Usuario[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  roles: Rol[] = [];
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: any = {};
  selectedRoles: number[] = [];
  private editId?: number;
  ngOnInit() { this.cargar(); this.api.get<Rol[]>('/roles').subscribe(r => this.roles = r); }
  cargar() {
    this.loading = true;
    this.pagina = 0;
    this.api.get<Usuario[]>('/usuarios').subscribe(r => { this.list = r; this.loading = false; });
  }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = { enabled: true }; this.selectedRoles = []; this.showForm.set(true); }
  editar(u: Usuario) { this.editId = u.id; this.editando.set(true); this.form = { username: u.username, email: u.email, enabled: u.enabled }; this.selectedRoles = this.roles.filter(r => u.roles.includes(r.codigo)).map(r => r.id!); this.showForm.set(true); }
  toggleRol(id: number) { const i = this.selectedRoles.indexOf(id); if (i >= 0) this.selectedRoles.splice(i, 1); else this.selectedRoles.push(id); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const body: any = { username: this.form.username, email: this.form.email, password: this.form.password || 'default123', rolIds: this.selectedRoles };
    const obs = this.editId ? this.api.put<Usuario>('/usuarios', this.editId, body) : this.api.post<Usuario>('/usuarios', body);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(u: Usuario) { if (confirm(`¿Eliminar usuario "${u.username}"?`)) this.api.delete('/usuarios', u.id!).subscribe({ next: () => this.cargar() }); }
}
