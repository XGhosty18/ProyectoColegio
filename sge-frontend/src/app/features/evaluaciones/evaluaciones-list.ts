import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { Evaluacion, Curso, Bimestre, TipoEvaluacion } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-evaluaciones-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Evaluaciones</h1></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="card">
      <div class="field"><label>Curso</label>
        <select [(ngModel)]="cursoId" (change)="cargar()">
          <option value="">Seleccione curso...</option>
          @for (c of cursos; track c.id) { <option [value]="c.id">{{ c.gradoNombre }} - {{ c.materiaNombre }}{{ c.seccionNombre ? ' ('+c.seccionNombre+')' : '' }}</option> }
        </select>
      </div>
      <button class="btn btn-primary" (click)="nuevo()" [disabled]="!cursoId">Nueva Evaluación</button>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Nombre</th><th>Bimestre</th><th>Tipo</th><th>Fecha</th><th>Ponderación</th><th></th></tr></thead>
        <tbody>
          @for (e of paginaActual; track e.id) {
            <tr>
              <td>{{ e.id }}</td><td>{{ e.nombre }}</td>
              <td>{{ e.bimestreNombre ?? '—' }}</td><td>{{ e.tipoEvaluacionNombre ?? '—' }}</td>
              <td>{{ e.fecha }}</td><td>{{ e.ponderacionOverride ?? '—' }}</td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(e)">Editar</button>
                <button class="btn-sm btn-ok" (click)="notas(e)">Notas</button>
                <button class="btn-sm btn-danger" (click)="eliminar(e)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">{{ cursoId ? 'No hay evaluaciones' : 'Seleccione un curso' }}</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Evaluación' : 'Nueva Evaluación'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Nombre</label><input [(ngModel)]="form.nombre" name="nombre" required></div>
        <div class="row">
          <div class="field"><label>Bimestre</label>
            <select [(ngModel)]="form.bimestreId" name="bimestreId" required>
              <option value="">Seleccione...</option>
              @for (b of bimestres; track b.id) { <option [value]="b.id">{{ b.nombre }} (#{{ b.numero }})</option> }
            </select>
          </div>
          <div class="field"><label>Tipo</label>
            <select [(ngModel)]="form.tipoEvaluacionId" name="tipoEvaluacionId" required>
              <option value="">Seleccione...</option>
              @for (t of tipos; track t.id) { <option [value]="t.id">{{ t.nombre }} ({{ t.pesoPorcentaje }}%)</option> }
            </select>
          </div>
        </div>
        <div class="row">
          <div class="field"><label>Fecha</label><input type="date" [(ngModel)]="form.fecha" name="fecha" required></div>
          <div class="field"><label>Ponderación (opcional)</label><input type="number" step="0.01" [(ngModel)]="form.ponderacionOverride" name="ponderacionOverride"></div>
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
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; white-space: nowrap; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    .btn-sm { padding: .25rem .625rem; border: 1px solid #dadce0; border-radius: 4px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .btn-ok { color: #1e7e34; border-color: #c3e6cb; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .row { display: flex; gap: .75rem; }
    .row > * { flex: 1; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class EvaluacionesList implements OnInit {
  private api = inject(ApiService);
  private router = inject(Router);
  list: Evaluacion[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  cursos: Curso[] = [];
  bimestres: Bimestre[] = [];
  tipos: TipoEvaluacion[] = [];
  cursoId = 0;
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<Evaluacion> = {};
  private editId?: number;

  ngOnInit() {
    this.api.get<Curso[]>('/cursos').subscribe(r => this.cursos = r);
    this.api.get<TipoEvaluacion[]>('/tipos-evaluacion').subscribe(r => this.tipos = r);
  }
  cargar() {
    if (this.cursoId) {
      this.loading = true;
      this.pagina = 0;
      this.api.get<Evaluacion[]>('/evaluaciones/curso/' + this.cursoId).subscribe(r => { this.list = r; this.loading = false; });
      this.api.get<Bimestre[]>('/bimestres', { periodoId: this.cursos.find(c => c.id === this.cursoId)?.periodoId ?? 0 }).subscribe(r => this.bimestres = r);
    }
  }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = { cursoId: this.cursoId }; this.showForm.set(true); }
  editar(e: Evaluacion) { this.editId = e.id; this.editando.set(true); this.form = { ...e }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  notas(e: Evaluacion) { this.router.navigate(['/notas', e.id]); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<Evaluacion>('/evaluaciones', this.editId, this.form) : this.api.post<Evaluacion>('/evaluaciones', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(e: Evaluacion) { if (confirm(`¿Eliminar "${e.nombre}"?`)) this.api.delete('/evaluaciones', e.id!).subscribe({ next: () => this.cargar() }); }
}
