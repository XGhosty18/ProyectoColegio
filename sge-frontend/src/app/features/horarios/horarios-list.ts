import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { HorarioBloque, Curso, Aula } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-horarios-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header">
      <h1>Horarios (Bloques)</h1>
      <button class="btn btn-primary" (click)="nuevo()">Nuevo Bloque</button>
    </div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Curso</th><th>Día</th><th>Inicio</th><th>Fin</th><th>Aula</th><th></th></tr></thead>
        <tbody>
          @for (h of paginaActual; track h.id) {
            <tr>
              <td>{{ h.id }}</td><td>{{ cursoLabel(h.cursoId) }}</td>
              <td>{{ diaLabel(h.diaSemana) }}</td><td>{{ h.horaInicio }}</td><td>{{ h.horaFin }}</td>
              <td>{{ h.aulaId ? aulaLabel(h.aulaId) : '—' }}</td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(h)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(h)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">No hay bloques horarios</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Bloque' : 'Nuevo Bloque'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Curso</label>
          <select [(ngModel)]="form.cursoId" name="cursoId" required>
            <option value="">Seleccione...</option>
            @for (c of cursos; track c.id) { <option [value]="c.id">{{ cursoLabel(c.id!) }}</option> }
          </select>
        </div>
        <div class="field"><label>Día</label>
          <select [(ngModel)]="form.diaSemana" name="diaSemana" required>
            <option value="">Seleccione...</option>
            <option value="1">Lunes</option><option value="2">Martes</option><option value="3">Miércoles</option>
            <option value="4">Jueves</option><option value="5">Viernes</option><option value="6">Sábado</option>
          </select>
        </div>
        <div class="field"><label>Hora Inicio</label><input type="time" [(ngModel)]="form.horaInicio" name="horaInicio" required></div>
        <div class="field"><label>Hora Fin</label><input type="time" [(ngModel)]="form.horaFin" name="horaFin" required></div>
        <div class="field"><label>Aula</label>
          <select [(ngModel)]="form.aulaId" name="aulaId">
            <option value="">Sin asignar</option>
            @for (a of aulas; track a.id) { <option [value]="a.id">{{ a.nombre }} ({{ a.codigo }})</option> }
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
    .form { display: flex; flex-direction: column; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: .25rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input, select { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class HorariosList implements OnInit {
  private api = inject(ApiService);
  list: HorarioBloque[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  cursos: Curso[] = [];
  aulas: Aula[] = [];
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<HorarioBloque> = {};
  private editId?: number;
  dias = ['', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];

  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }

  ngOnInit() { this.cargar(); this.api.get<Curso[]>('/cursos').subscribe(r => this.cursos = r); this.api.get<Aula[]>('/aulas').subscribe(r => this.aulas = r); }
  cargar() { this.loading = true; this.api.get<HorarioBloque[]>('/horarios').subscribe(r => { this.list = r; this.loading = false; }); }
  cursoLabel(id: number) { const c = this.cursos.find(c => c.id === id); return c ? `Curso #${c.id}` : '—'; }
  aulaLabel(id: number) { return this.aulas.find(a => a.id === id)?.nombre ?? '—'; }
  diaLabel(n: number) { return this.dias[n] ?? '—'; }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = {}; this.showForm.set(true); }
  editar(h: HorarioBloque) { this.editId = h.id; this.editando.set(true); this.form = { ...h }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const body = { ...this.form };
    if (!body.aulaId) delete body.aulaId;
    const obs = this.editId ? this.api.put<HorarioBloque>('/horarios', this.editId, body) : this.api.post<HorarioBloque>('/horarios', body);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(h: HorarioBloque) { if (confirm('¿Eliminar bloque?')) this.api.delete('/horarios', h.id!).subscribe({ next: () => this.cargar() }); }
}
