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
      <div>
        <h1>Horarios Academicos</h1>
        <p class="subtitle">Gestion y visualizacion dinamica de bloques de clases</p>
      </div>
      <div class="header-actions">
        <div class="toggle-group">
          <button class="btn-toggle" [class.active]="vista() === 'grilla'" (click)="vista.set('grilla')">Grilla Semanal</button>
          <button class="btn-toggle" [class.active]="vista() === 'tabla'" (click)="vista.set('tabla')">Lista de Bloques</button>
        </div>
        <button class="btn btn-primary" (click)="nuevo()">+ Nuevo Bloque</button>
      </div>
    </div>

    @if (loading) { <div class="loading">Cargando horario escolar...</div> }

    @if (vista() === 'grilla') {
      <div class="grid-wrap">
        <div class="weekly-grid">
          <div class="grid-header">
            <div class="time-col-header">Hora</div>
            @for (dia of diasSemana; track dia.id) {
              <div class="day-col-header">
                <span class="day-name">{{ dia.nombre }}</span>
              </div>
            }
          </div>
          <div class="grid-body">
            @for (slot of franjasHorarias; track slot.inicio) {
              <div class="grid-row">
                <div class="time-cell">
                  <span class="time-start">{{ slot.inicio }}</span>
                  <span class="time-end">{{ slot.fin }}</span>
                </div>
                @for (dia of diasSemana; track dia.id) {
                  <div class="schedule-cell">
                    @for (h of getBloquesParaFranja(dia.id, slot.inicio); track h.id) {
                      <div class="block-card">
                        <div class="block-title">{{ cursoLabel(h.cursoId) }}</div>
                        <div class="block-detail">📍 {{ h.aulaId ? aulaLabel(h.aulaId) : 'Sin Aula' }}</div>
                        <div class="block-actions">
                          <button class="btn-icon" (click)="editar(h)" title="Editar">✏️</button>
                          <button class="btn-icon danger" (click)="eliminar(h)" title="Eliminar">🗑️</button>
                        </div>
                      </div>
                    }
                  </div>
                }
              </div>
            }
          </div>
        </div>
      </div>
    } @else {
      <div class="table-wrap">
        <table>
          <thead><tr><th>ID</th><th>Curso</th><th>Día</th><th>Inicio</th><th>Fin</th><th>Aula</th><th>Acciones</th></tr></thead>
          <tbody>
            @for (h of list; track h.id) {
              <tr>
                <td>{{ h.id }}</td>
                <td><strong>{{ cursoLabel(h.cursoId) }}</strong></td>
                <td><span class="badge">{{ diaLabel(h.diaSemana) }}</span></td>
                <td>{{ h.horaInicio }}</td>
                <td>{{ h.horaFin }}</td>
                <td>{{ h.aulaId ? aulaLabel(h.aulaId) : '—' }}</td>
                <td class="actions">
                  <button class="btn-sm" (click)="editar(h)">Editar</button>
                  <button class="btn-sm btn-danger" (click)="eliminar(h)">Eliminar</button>
                </td>
              </tr>
            } @empty { <tr><td colspan="7" class="empty">No hay bloques horarios registrados</td></tr> }
          </tbody>
        </table>
      </div>
    }

    <sge-modal [open]="showForm()" [title]="editando() ? 'Editar Bloque Horario' : 'Nuevo Bloque Horario'" (close)="cerrarForm()">
      <form (ngSubmit)="guardar()" class="form">
        <div class="field"><label>Curso</label>
          <select [(ngModel)]="form.cursoId" name="cursoId" required>
            <option value="">Seleccione un curso...</option>
            @for (c of cursos; track c.id) { <option [value]="c.id">{{ cursoLabel(c.id!) }}</option> }
          </select>
        </div>
        <div class="field"><label>Día de la Semana</label>
          <select [(ngModel)]="form.diaSemana" name="diaSemana" required>
            <option value="">Seleccione...</option>
            <option value="1">Lunes</option><option value="2">Martes</option><option value="3">Miércoles</option>
            <option value="4">Jueves</option><option value="5">Viernes</option>
          </select>
        </div>
        <div class="row">
          <div class="field"><label>Hora Inicio</label><input type="time" [(ngModel)]="form.horaInicio" name="horaInicio" required></div>
          <div class="field"><label>Hora Fin</label><input type="time" [(ngModel)]="form.horaFin" name="horaFin" required></div>
        </div>
        <div class="field"><label>Aula Asignada</label>
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
    .subtitle { margin: 0; font-size: 0.875rem; color: var(--text-muted); }
    .header-actions { display: flex; align-items: center; gap: 1rem; }
    .toggle-group { display: flex; background: #e2e8f0; padding: 3px; border-radius: var(--radius-sm); }
    .btn-toggle { border: none; background: transparent; padding: 0.4rem 0.85rem; font-size: 0.8125rem; font-weight: 600; color: var(--text-muted); cursor: pointer; border-radius: 4px; transition: all var(--transition-fast); }
    .btn-toggle.active { background: #ffffff; color: var(--primary); box-shadow: var(--shadow-sm); }
    
    .grid-wrap { background: #ffffff; border-radius: var(--radius-md); border: 1px solid var(--border-subtle); box-shadow: var(--shadow-sm); overflow: hidden; }
    .weekly-grid { display: flex; flex-direction: column; width: 100%; min-width: 700px; }
    .grid-header { display: grid; grid-template-columns: 90px repeat(5, 1fr); background: hsl(220, 20%, 97%); border-bottom: 1px solid var(--border-subtle); }
    .time-col-header, .day-col-header { padding: 0.875rem; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; color: var(--text-muted); text-align: center; }
    .grid-body { display: flex; flex-direction: column; }
    .grid-row { display: grid; grid-template-columns: 90px repeat(5, 1fr); border-bottom: 1px solid var(--border-subtle); min-height: 85px; }
    .time-cell { display: flex; flex-direction: column; justify-content: center; align-items: center; background: hsl(220, 20%, 99%); border-right: 1px solid var(--border-subtle); font-size: 0.75rem; font-weight: 600; color: var(--text-muted); padding: 0.5rem; }
    .schedule-cell { border-right: 1px solid var(--border-subtle); padding: 0.35rem; display: flex; flex-direction: column; gap: 0.35rem; }
    
    .block-card { background: var(--primary-light); border-left: 3px solid var(--primary); border-radius: var(--radius-sm); padding: 0.5rem; position: relative; transition: transform var(--transition-fast); }
    .block-card:hover { transform: translateY(-1px); box-shadow: var(--shadow-sm); }
    .block-title { font-size: 0.8125rem; font-weight: 700; color: var(--primary-hover); line-height: 1.2; }
    .block-detail { font-size: 0.725rem; color: var(--text-muted); margin-top: 0.2rem; }
    .block-actions { display: flex; gap: 0.25rem; margin-top: 0.35rem; justify-content: flex-end; }
    .btn-icon { border: none; background: transparent; cursor: pointer; font-size: 0.75rem; padding: 2px 4px; border-radius: 3px; }
    .btn-icon:hover { background: rgba(0,0,0,0.06); }
    
    .loading { text-align: center; padding: 2rem; color: var(--text-muted); }
    .actions { display: flex; gap: 0.375rem; }
    .form { display: flex; flex-direction: column; gap: 0.85rem; }
    .row { display: flex; gap: 0.75rem; }
    .row > * { flex: 1; }
    .field { display: flex; flex-direction: column; gap: 0.25rem; }
    label { font-size: 0.8125rem; color: var(--text-main); font-weight: 600; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 0.5rem; }
  `]
})
export class HorariosList implements OnInit {
  private api = inject(ApiService);
  list: HorarioBloque[] = [];
  loading = false;
  cursos: Curso[] = [];
  aulas: Aula[] = [];
  vista = signal<'grilla' | 'tabla'>('grilla');

  showForm = signal(false);
  editando = signal(false);
  saving = false;
  form: Partial<HorarioBloque> = {};
  private editId?: number;

  diasSemana = [
    { id: 1, nombre: 'Lunes' },
    { id: 2, nombre: 'Martes' },
    { id: 3, nombre: 'Miércoles' },
    { id: 4, nombre: 'Jueves' },
    { id: 5, nombre: 'Viernes' }
  ];

  franjasHorarias = [
    { inicio: '08:00', fin: '08:45' },
    { inicio: '08:45', fin: '09:30' },
    { inicio: '09:30', fin: '10:15' },
    { inicio: '10:30', fin: '11:15' },
    { inicio: '11:15', fin: '12:00' },
    { inicio: '12:00', fin: '12:45' }
  ];

  ngOnInit() {
    this.cargar();
    this.api.get<Curso[]>('/cursos').subscribe(r => this.cursos = r);
    this.api.get<Aula[]>('/aulas').subscribe(r => this.aulas = r);
  }

  cargar() {
    this.loading = true;
    this.api.get<HorarioBloque[]>('/horarios').subscribe({
      next: (r) => { this.list = r; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  getBloquesParaFranja(diaId: number, horaIni: string): HorarioBloque[] {
    return this.list.filter(h => Number(h.diaSemana) === diaId && h.horaInicio.startsWith(horaIni));
  }

  cursoLabel(id: number) {
    const c = this.cursos.find(c => c.id === id);
    return c ? `${c.materiaNombre || 'Materia'} (${c.gradoNombre || ''} ${c.seccionNombre || ''})` : `Curso #${id}`;
  }

  aulaLabel(id: number) {
    return this.aulas.find(a => a.id === id)?.nombre ?? '—';
  }

  diaLabel(n: number) {
    const d = this.diasSemana.find(item => item.id === Number(n));
    return d ? d.nombre : '—';
  }

  nuevo() { this.editId = undefined; this.editando.set(false); this.form = {}; this.showForm.set(true); }
  editar(h: HorarioBloque) { this.editId = h.id; this.editando.set(true); this.form = { ...h }; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }

  guardar() {
    this.saving = true;
    const body = { ...this.form };
    if (!body.aulaId) delete body.aulaId;
    const obs = this.editId
      ? this.api.put<HorarioBloque>('/horarios', this.editId, body)
      : this.api.post<HorarioBloque>('/horarios', body);

    obs.subscribe({
      next: () => { this.cargar(); this.cerrarForm(); this.saving = false; },
      error: (err) => {
        const bodyErr = err.error;
        alert(bodyErr?.detail || 'Error al guardar bloque horario');
        this.saving = false;
      }
    });
  }

  eliminar(h: HorarioBloque) {
    if (confirm('¿Eliminar bloque horario?')) {
      this.api.delete('/horarios', h.id!).subscribe({ next: () => this.cargar() });
    }
  }
}
