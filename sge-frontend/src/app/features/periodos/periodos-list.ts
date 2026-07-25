import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { PeriodoAcademico } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-periodos-list',
  imports: [DatePipe, FormsModule, Modal],
  template: `
    <div class="page-header">
      <h1>Periodos Académicos</h1>
      <button class="btn btn-primary" (click)="nuevo()">Nuevo Período</button>
    </div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Nombre</th><th>Código</th><th>Inicio</th><th>Fin</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          @for (p of paginaActual; track p.id) {
            <tr>
              <td>{{ p.id }}</td><td>{{ p.nombre }}</td><td>{{ p.codigo }}</td><td>{{ p.fechaInicio | date }}</td><td>{{ p.fechaFin | date }}</td>
              <td><span class="badge">{{ p.estado }}</span></td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(p)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(p)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">No hay periodos registrados</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Período' : 'Nuevo Período'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Nombre</label><input [(ngModel)]="form.nombre" name="nombre" required></div>
        <div class="field"><label>Código</label><input [(ngModel)]="form.codigo" name="codigo" required></div>
        <div class="field"><label>Fecha Inicio</label><input type="date" [(ngModel)]="form.fechaInicio" name="fechaInicio" required></div>
        <div class="field"><label>Fecha Fin</label><input type="date" [(ngModel)]="form.fechaFin" name="fechaFin" required></div>
        <div class="field">
          <label>Estado</label>
          <select [(ngModel)]="form.estado" name="estado" required>
            <option value="PLANIFICACION">Planificación</option>
            <option value="ACTIVO">Activo</option>
            <option value="CERRADO">Cerrado</option>
            <option value="FINALIZADO">Finalizado</option>
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
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input, select { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
    .loading { text-align: center; padding: 2rem; color: #999; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; padding: 1rem; font-size: .875rem; color: #666; }
  `]
})
export class PeriodosList implements OnInit {
  private api = inject(ApiService);
  list: PeriodoAcademico[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  get paginaActual() { return this.list.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.list.length / this.pageSize); }
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<PeriodoAcademico> = {};
  private editId?: number;

  ngOnInit() { this.cargar(); }
  cargar() { this.loading = true; this.api.get<PeriodoAcademico[]>('/periodos').subscribe(r => { this.list = r; this.loading = false; }); }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = { estado: 'PLANIFICACION' }; this.showForm.set(true); }
  editar(p: PeriodoAcademico) { this.editId = p.id; this.editando.set(true); this.form = { ...p }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<PeriodoAcademico>('/periodos', this.editId, this.form) : this.api.post<PeriodoAcademico>('/periodos', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(p: PeriodoAcademico) { if (confirm(`¿Eliminar "${p.nombre}"?`)) this.api.delete('/periodos', p.id!).subscribe({ next: () => this.cargar() }); }
}
