import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Bimestre, PeriodoAcademico } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-bimestres-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Bimestres</h1></div>
    @if (loading) { <div class="loading">Cargando...</div> }
    <div class="card">
      <div class="field"><label>Período</label>
        <select [(ngModel)]="periodoId" (change)="cargar()">
          <option value="">Seleccione período...</option>
          @for (p of periodos; track p.id) { <option [value]="p.id">{{ p.nombre }} ({{ p.codigo }})</option> }
        </select>
      </div>
      <button class="btn btn-primary" (click)="nuevo()" [disabled]="!periodoId">Nuevo Bimestre</button>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Nombre</th><th>N°</th><th>Inicio</th><th>Fin</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          @for (b of paginaActual; track b.id) {
            <tr>
              <td>{{ b.id }}</td><td>{{ b.nombre }}</td><td>{{ b.numero }}</td>
              <td>{{ b.fechaInicio }}</td><td>{{ b.fechaFin }}</td>
              <td><span class="badge">{{ b.estado }}</span></td>
              <td class="actions">
                <button class="btn-sm" (click)="editar(b)">Editar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(b)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">Seleccione un período</td></tr> }
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
    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Bimestre' : 'Nuevo Bimestre'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Nombre</label><input [(ngModel)]="form.nombre" name="nombre" required></div>
        <div class="field"><label>Número</label><input type="number" [(ngModel)]="form.numero" name="numero" required></div>
        <div class="field"><label>Fecha Inicio</label><input type="date" [(ngModel)]="form.fechaInicio" name="fechaInicio" required></div>
        <div class="field"><label>Fecha Fin</label><input type="date" [(ngModel)]="form.fechaFin" name="fechaFin" required></div>
        <div class="field"><label>Estado</label>
          <select [(ngModel)]="form.estado" name="estado" required>
            <option value="ABIERTO">Abierto</option><option value="CERRADO">Cerrado</option><option value="PUBLICADO">Publicado</option>
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
export class BimestresList implements OnInit {
  private api = inject(ApiService);
  bimestres: Bimestre[] = [];
  loading = false;
  pagina = 0;
  readonly pageSize = 15;
  periodos: PeriodoAcademico[] = [];
  periodoId = 0;
  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<Bimestre> = { estado: 'ABIERTO' };
  private editId?: number;
  get paginaActual() { return this.bimestres.slice(this.pagina * this.pageSize, (this.pagina + 1) * this.pageSize); }
  get totalPaginas() { return Math.ceil(this.bimestres.length / this.pageSize); }

  ngOnInit() { this.api.get<PeriodoAcademico[]>('/periodos').subscribe(r => this.periodos = r); }
  cargar() { if (this.periodoId) { this.loading = true; this.api.get<Bimestre[]>('/bimestres', { periodoId: this.periodoId }).subscribe(r => { this.bimestres = r; this.loading = false; }); } }
  nuevo() { this.editId = undefined; this.editando.set(false); this.form = { estado: 'ABIERTO', periodoId: this.periodoId }; this.showForm.set(true); }
  editar(b: Bimestre) { this.editId = b.id; this.editando.set(true); this.form = { ...b }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  guardar() {
    this.saving = true;
    const obs = this.editId ? this.api.put<Bimestre>('/bimestres', this.editId, this.form) : this.api.post<Bimestre>('/bimestres', this.form);
    obs.subscribe({ next: () => { this.cargar(); this.cerrarForm(); this.saving = false; }, error: () => { alert('Error'); this.saving = false; } });
  }
  eliminar(b: Bimestre) { if (confirm(`¿Eliminar "${b.nombre}"?`)) this.api.delete('/bimestres', b.id!).subscribe({ next: () => this.cargar() }); }
}
