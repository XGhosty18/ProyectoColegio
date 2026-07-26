import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Alumno, Padre, Page } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-alumnos-list',
  standalone: true,
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header">
      <h1>Alumnos</h1>
      <button class="btn btn-primary" (click)="nuevo()">Nuevo Alumno</button>
    </div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Código</th><th>Nombres</th><th>Apellidos</th><th>DNI</th><th>Estado</th><th>Padres</th><th></th></tr></thead>
        <tbody>
          @for (a of alumnos; track a.id) {
            <tr>
              <td>{{ a.id }}</td><td>{{ a.codigoEstudiante }}</td><td>{{ a.nombres }}</td><td>{{ a.apellidos }}</td><td>{{ a.dni }}</td>
              <td><span class="badge">{{ a.estadoActualNombre ?? '—' }}</span></td>
              <td><button class="btn-sm link" (click)="verPadres(a)">{{ (a.padres?.length || 0) }} padre(s)</button></td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(a)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(a)">Eliminar</button>
              </td>
            </tr>
          } @empty { @if (!loading) { <tr><td colspan="8" class="empty">No hay alumnos registrados</td></tr> } }
        </tbody>
      </table>
    </div>
    @if (totalPaginas > 1) {
      <div class="pagination">
        <button class="btn-sm" [disabled]="pagina === 0" (click)="cambiarPagina(pagina - 1)">Anterior</button>
        <span>Pág. {{ pagina + 1 }} de {{ totalPaginas }}</span>
        <button class="btn-sm" [disabled]="pagina >= totalPaginas - 1" (click)="cambiarPagina(pagina + 1)">Siguiente</button>
      </div>
    }
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Alumno' : 'Nuevo Alumno'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="row">
          <div class="field"><label>Nombres</label><input [(ngModel)]="form.nombres" name="nombres" required></div>
          <div class="field"><label>Apellidos</label><input [(ngModel)]="form.apellidos" name="apellidos" required></div>
        </div>
        <div class="row">
          <div class="field"><label>DNI</label><input [(ngModel)]="form.dni" name="dni" required></div>
          <div class="field"><label>Código Estudiante</label><input [(ngModel)]="form.codigoEstudiante" name="codigoEstudiante" required></div>
        </div>
        <div class="row">
          <div class="field"><label>Fecha Nac.</label><input type="date" [(ngModel)]="form.fechaNac" name="fechaNac" required></div>
          <div class="field"><label>Género</label>
            <select [(ngModel)]="form.genero" name="genero">
              <option value="">Seleccione...</option><option value="M">Masculino</option><option value="F">Femenino</option>
            </select>
          </div>
        </div>
        <div class="field"><label>Teléfono</label><input [(ngModel)]="form.telefono" name="telefono"></div>
        <div class="field"><label>Dirección</label><input [(ngModel)]="form.direccion" name="direccion"></div>
        <div class="form-actions">
          <button type="button" class="btn btn-cancel" (click)="cerrarForm()">Cancelar</button>
          <button type="submit" class="btn btn-primary" [disabled]="saving">{{ saving ? 'Guardando...' : 'Guardar' }}</button>
        </div>
      </form>
    </sge-modal>
    <sge-modal [open]="showPadres()" [title]="'Padres de ' + (alumnoPadresSeleccionado?.nombres || '')" (close)="cerrarPadres()">
      <div class="padres-list">
        @for (p of padresActuales; track p.id) {
          <div class="padre-item">
            <span>{{ p.nombres }} {{ p.apellidos }} ({{ p.dni }}) — {{ p.parentesco || '—' }}</span>
            <button class="btn-sm btn-danger" (click)="desasignarPadre(p)">Quitar</button>
          </div>
        } @empty { <div class="empty">Sin padres asignados</div> }
      </div>
      <div class="asignar">
        <select [(ngModel)]="nuevoPadreId"><option value="">Seleccione padre...</option>
          @for (p of todosPadres; track p.id) { <option [value]="p.id">{{ p.nombres }} {{ p.apellidos }} ({{ p.dni }})</option> }
        </select>
        <button class="btn btn-primary" [disabled]="!nuevoPadreId" (click)="asignarPadre()">Asignar</button>
      </div>
    </sge-modal>
  `,
  styles: [`
    .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: .03em; border-bottom: 2px solid #e9ecef; }
    td { padding: .75rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .badge { display: inline-block; padding: .1875rem .5rem; background: #e8f0fe; color: #1a73e8; border-radius: 99px; font-size: .75rem; font-weight: 600; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    .btn-sm { padding: .25rem .625rem; border: 1px solid #dadce0; border-radius: 4px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-sm.link { border: none; color: #1a73e8; text-decoration: underline; padding: 0; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .row { display: flex; gap: .75rem; }
    .row > * { flex: 1; }
    .field { display: flex; flex-direction: column; gap: .25rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input, select { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .padres-list { display: flex; flex-direction: column; gap: .5rem; margin-bottom: 1rem; }
    .padre-item { display: flex; justify-content: space-between; align-items: center; padding: .5rem; background: #f8f9fa; border-radius: 6px; font-size: .875rem; }
    .asignar { display: flex; gap: .5rem; }
    .asignar select { flex: 1; }
  `]
})
export class AlumnosList implements OnInit {
  private api = inject(ApiService);
  alumnos: Alumno[] = [];
  loading = false;
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<Alumno> = {};
  private editId?: number;
  pagina = 0;
  readonly pageSize = 15;
  totalPaginas = 1;

  showPadres = signal(false);
  alumnoPadresSeleccionado?: Alumno;
  padresActuales: Padre[] = [];
  todosPadres: Padre[] = [];
  nuevoPadreId = 0;

  ngOnInit() { this.cargar(); this.api.get<Padre[]>('/padres').subscribe(r => this.todosPadres = r); }

  cargar() {
    this.loading = true;
    this.api.get<Page<Alumno> | Alumno[]>(`/alumnos?page=${this.pagina}&size=${this.pageSize}`).subscribe({
      next: (res) => {
        if ('content' in res) {
          this.alumnos = res.content;
          this.totalPaginas = res.totalPages || 1;
        } else {
          this.alumnos = res;
          this.totalPaginas = Math.ceil(res.length / this.pageSize) || 1;
        }
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPaginas) {
      this.pagina = nuevaPagina;
      this.cargar();
    }
  }

  nuevo() { this.editId = undefined; this.editando.set(false); this.form = {}; this.showForm.set(true); }
  editar(a: Alumno) { this.editId = a.id; this.editando.set(true); this.form = { ...a }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }

  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<Alumno>('/alumnos', this.editId, this.form) : this.api.post<Alumno>('/alumnos', this.form);
    obs.subscribe({
      next: () => { this.cargar(); this.cerrarForm(); this.saving = false; },
      error: (err) => {
        const body = err.error;
        let msg = 'Error al guardar';
        if (body?.title) msg = body.title;
        if (body?.detail) msg = body.detail;
        if (body?.errores) msg = body.errores.map((e: any) => e.campo + ': ' + e.error).join('\n');
        if (body?.codigo) msg = body.codigo + ': ' + (body.detail || msg);
        alert(msg);
        this.saving = false;
      }
    });
  }

  eliminar(a: Alumno) { if (confirm(`¿Eliminar a "${a.nombres} ${a.apellidos}"?`)) this.api.delete('/alumnos', a.id!).subscribe({ next: () => this.cargar() }); }

  verPadres(a: Alumno) {
    this.alumnoPadresSeleccionado = a;
    this.padresActuales = a.padres || [];
    this.showPadres.set(true);
  }

  cerrarPadres() { this.showPadres.set(false); }

  asignarPadre() {
    if (!this.alumnoPadresSeleccionado?.id || !this.nuevoPadreId) return;
    this.api.post('/alumnos/' + this.alumnoPadresSeleccionado.id + '/padres', { padreId: this.nuevoPadreId }).subscribe({
      next: () => {
        this.cargar();
        this.cerrarPadres();
        this.nuevoPadreId = 0;
      }
    });
  }

  desasignarPadre(p: Padre) {
    if (!this.alumnoPadresSeleccionado?.id) return;
    this.api.delete('/alumnos/' + this.alumnoPadresSeleccionado.id + '/padres', p.id!).subscribe({
      next: () => {
        this.cargar();
        this.cerrarPadres();
      }
    });
  }
}
