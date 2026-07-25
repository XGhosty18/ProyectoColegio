import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { SesionClase, Curso, HorarioBloque } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-sesiones-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Sesiones de Clase</h1></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="card">
      <div class="field"><label>Curso</label>
        <select [(ngModel)]="cursoId" (change)="cargar()">
          <option value="">Seleccione curso...</option>
          @for (c of cursos; track c.id) { <option [value]="c.id">{{ c.gradoNombre }} - {{ c.materiaNombre }}{{ c.seccionNombre ? ' ('+c.seccionNombre+')' : '' }}</option> }
        </select>
      </div>
      <button class="btn btn-primary" (click)="nuevo()" [disabled]="!cursoId">Nueva Sesión</button>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Fecha</th><th>Inicio</th><th>Fin</th><th>Tema</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          @for (s of paginaActual; track s.id) {
            <tr>
              <td>{{ s.id }}</td><td>{{ s.fecha }}</td>
              <td>{{ s.horaInicio ?? '—' }}</td><td>{{ s.horaFin ?? '—' }}</td>
              <td>{{ s.tema ?? '—' }}</td>
              <td><span class="badge">{{ s.estado }}</span></td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(s)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(s)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">{{ cursoId ? 'No hay sesiones' : 'Seleccione un curso' }}</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Sesión' : 'Nueva Sesión'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Fecha</label><input type="date" [(ngModel)]="form.fecha" name="fecha" required></div>
        <div class="field"><label>Hora Inicio</label><input type="time" [(ngModel)]="form.horaInicio" name="horaInicio"></div>
        <div class="field"><label>Hora Fin</label><input type="time" [(ngModel)]="form.horaFin" name="horaFin"></div>
        <div class="field"><label>Tema</label><input [(ngModel)]="form.tema" name="tema"></div>
        <div class="field"><label>Horario Bloque</label>
          <select [(ngModel)]="form.horarioBloqueId" name="horarioBloqueId">
            <option value="">Sin bloque</option>
            @for (h of horarios; track h.id) { <option [value]="h.id">#{{ h.id }} (Día {{ h.diaSemana }} {{ h.horaInicio }})</option> }
          </select>
        </div>
        <div class="field"><label>Estado</label>
          <select [(ngModel)]="form.estado" name="estado" required>
            <option value="PROGRAMADA">Programada</option><option value="EN_CURSO">En curso</option>
            <option value="CONFIRMADA">Confirmada</option><option value="ANULADA">Anulada</option>
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
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0 0 1rem; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; display: flex; gap: 1rem; align-items: end; }
    .field { display: flex; flex-direction: column; gap: .25rem; flex: 1; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select, input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: .03em; border-bottom: 2px solid #e9ecef; }
    td { padding: .75rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .badge { display: inline-block; padding: .1875rem .5rem; background: #e8f0fe; color: #1a73e8; border-radius: 99px; font-size: .75rem; font-weight: 600; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; white-space: nowrap; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    .btn-sm { padding: .25rem .625rem; border: 1px solid #dadce0; border-radius: 4px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .form { display: flex; flex-direction: column; gap: 1rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class SesionesList implements OnInit {
  private api = inject(ApiService);
  sesiones: SesionClase[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  cursos: Curso[] = [];
  horarios: HorarioBloque[] = [];
  cursoId = 0;
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<SesionClase> = { estado: 'PROGRAMADA' };
  private editId?: number;
  get paginaActual() { return this.sesiones.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.sesiones.length / this.pageSize); }

  ngOnInit() { this.api.get<Curso[]>('/cursos').subscribe(r => this.cursos = r); this.api.get<HorarioBloque[]>('/horarios').subscribe(r => this.horarios = r); }
  cargar() { if (this.cursoId) { this.loading = true; this.api.get<SesionClase[]>('/sesiones/curso/' + this.cursoId).subscribe(r => { this.sesiones = r; this.loading = false; }); } }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = { estado: 'PROGRAMADA', cursoId: this.cursoId }; this.showForm.set(true); }
  editar(s: SesionClase) { this.editId = s.id; this.editando.set(true); this.form = { ...s }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const body = { ...this.form };
    if (!body.horarioBloqueId) delete body.horarioBloqueId;
    const obs = this.editId ? this.api.put<SesionClase>('/sesiones', this.editId, body) : this.api.post<SesionClase>('/sesiones', body);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(s: SesionClase) { if (confirm('¿Eliminar sesión?')) this.api.delete('/sesiones', s.id!).subscribe({ next: () => this.cargar() }); }
}
