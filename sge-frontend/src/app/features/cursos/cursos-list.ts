import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Curso, Grado, Materia, Seccion, PeriodoAcademico, Docente, Aula } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-cursos-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header">
      <h1>Cursos</h1>
      <button class="btn btn-primary" (click)="nuevo()">Nuevo Curso</button>
    </div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Grado</th><th>Sección</th><th>Materia</th><th>Período</th><th>Docente</th><th>Aula</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          @for (c of paginaActual; track c.id) {
            <tr>
              <td>{{ c.id }}</td>
              <td>{{ c.gradoNombre }}</td><td>{{ c.seccionNombre }}</td><td>{{ c.materiaNombre }}</td><td>{{ c.periodoNombre }}</td>
              <td>{{ c.docenteNombre ?? '—' }}</td><td>{{ c.aulaNombre ?? '—' }}</td>
              <td><span class="badge">{{ c.estado }}</span></td>
              <td class="actions">
                <button class="btn-sm" (click)="asignar(c)">Asignar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(c)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="9" class="empty">No hay cursos</td></tr> }
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
    <sge-modal [open]="showForm()" title="Nuevo Curso" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Período</label>
          <select [(ngModel)]="form.periodoId" name="periodoId" required>
            <option value="">Seleccione...</option>
            @for (p of periodos; track p.id) { <option [value]="p.id">{{ p.nombre }} ({{ p.codigo }})</option> }
          </select>
        </div>
        <div class="field"><label>Grado</label>
          <select [(ngModel)]="form.gradoId" name="gradoId" required>
            <option value="">Seleccione...</option>
            @for (g of grados; track g.id) { <option [value]="g.id">{{ g.nombre }} ({{ g.nivel }})</option> }
          </select>
        </div>
        <div class="field"><label>Sección</label>
          <select [(ngModel)]="form.seccionId" name="seccionId" required>
            <option value="">Seleccione...</option>
            @for (s of secciones; track s.id) { <option [value]="s.id">{{ s.nombre }}</option> }
          </select>
        </div>
        <div class="field"><label>Materia</label>
          <select [(ngModel)]="form.materiaId" name="materiaId" required>
            <option value="">Seleccione...</option>
            @for (m of materias; track m.id) { <option [value]="m.id">{{ m.nombre }} ({{ m.codigo }})</option> }
          </select>
        </div>
        <div class="field"><label>Docente</label>
          <select [(ngModel)]="form.docenteId" name="docenteId">
            <option value="">Sin asignar</option>
            @for (d of docentes; track d.id) { <option [value]="d.id">{{ d.nombres }} {{ d.apellidos }}</option> }
          </select>
        </div>
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
    <sge-modal [open]="showAsignar()" title="Asignar Recursos" (close)="cerrarAsignar()">
      <form (ngSubmit)="guardarAsignacion()" class="form">
        <p>Curso #{{ asignarCursoId }} — {{ asignarCursoNombre }}</p>
        <div class="field"><label>Docente</label>
          <select [(ngModel)]="asignarDocenteId" name="asignarDocenteId">
            <option value="">Sin asignar</option>
            @for (d of docentes; track d.id) { <option [value]="d.id">{{ d.nombres }} {{ d.apellidos }}</option> }
          </select>
        </div>
        <div class="field"><label>Aula</label>
          <select [(ngModel)]="asignarAulaId" name="asignarAulaId">
            <option value="">Sin asignar</option>
            @for (a of aulas; track a.id) { <option [value]="a.id">{{ a.nombre }} ({{ a.codigo }})</option> }
          </select>
        </div>
        <div class="form-actions">
          <button type="button" class="btn btn-cancel" (click)="cerrarAsignar()">Cancelar</button>
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
    .badge { display: inline-block; padding: .1875rem .5rem; background: #e8f0fe; color: #1a73e8; border-radius: 99px; font-size: .75rem; font-weight: 600; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn-sm { padding: .25rem .625rem; border: 1px solid #dadce0; border-radius: 4px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .form { display: flex; flex-direction: column; gap: 1rem; }
    .field { display: flex; flex-direction: column; gap: .25rem; }
    p { margin: 0; font-size: .875rem; color: #666; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input, select { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class CursosList implements OnInit {
  private api = inject(ApiService);
  cursos: Curso[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  grados: Grado[] = []; materias: Materia[] = []; secciones: Seccion[] = []; periodos: PeriodoAcademico[] = []; docentes: Docente[] = []; aulas: Aula[] = [];
  showForm = signal(false); editando = signal(false); saving = false;
  form: any = {};
  showAsignar = signal(false);
  asignarCursoId = 0; asignarCursoNombre = ''; asignarDocenteId: number | string = ''; asignarAulaId: number | string = '';

  get paginaActual() { return this.cursos.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.cursos.length / this.pageSize); }

  ngOnInit() {
    this.cargar();
    this.api.get<Grado[]>('/grados').subscribe(r => this.grados = r);
    this.api.get<Materia[]>('/materias').subscribe(r => this.materias = r);
    this.api.get<Seccion[]>('/secciones').subscribe(r => this.secciones = r);
    this.api.get<PeriodoAcademico[]>('/periodos').subscribe(r => this.periodos = r);
    this.api.get<Docente[]>('/docentes').subscribe(r => this.docentes = r);
    this.api.get<Aula[]>('/aulas').subscribe(r => this.aulas = r);
  }
  cargar() { this.loading = true; this.api.get<Curso[]>('/cursos').subscribe(r => { this.cursos = r; this.loading = false; }); }
  nuevo() { this.form = {}; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const body: any = { periodoId: this.form.periodoId, gradoId: this.form.gradoId, seccionId: this.form.seccionId, materiaId: this.form.materiaId };
    if (this.form.docenteId) body.docenteId = Number(this.form.docenteId);
    if (this.form.aulaId) body.aulaId = Number(this.form.aulaId);
    this.api.post<Curso>('/cursos', body).subscribe({
      next: () => { this.cargar(); this.cerrarForm(); this.saving = false; },
      error: () => { alert('Error al crear'); this.saving = false; }
    });
  }
  asignar(c: Curso) { this.asignarCursoId = c.id!; this.asignarCursoNombre = c.gradoNombre + ' - ' + c.materiaNombre; this.asignarDocenteId = c.docenteId ?? ''; this.asignarAulaId = c.aulaId ?? ''; this.showAsignar.set(true); }
  cerrarAsignar() { this.showAsignar.set(false); }
  guardarAsignacion() {
    this.saving = true;
    const params: Record<string, number> = {};
    if (this.asignarDocenteId) params['docenteId'] = Number(this.asignarDocenteId);
    if (this.asignarAulaId) params['aulaId'] = Number(this.asignarAulaId);
    this.api.putParams<Curso>('/cursos/' + this.asignarCursoId + '/asignar-recursos', 0, params).subscribe({
      next: () => { this.cargar(); this.cerrarAsignar(); this.saving = false; },
      error: () => { alert('Error al asignar'); this.saving = false; }
    });
  }
  eliminar(c: Curso) { if (confirm(`¿Eliminar curso?`)) this.api.delete('/cursos', c.id!).subscribe({ next: () => this.cargar() }); }
}
