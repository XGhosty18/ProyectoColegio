import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ApiService } from '../../core/services/api.service';
import { Curso, Bimestre, PeriodoAcademico } from '../../core/models/types';

@Component({
  selector: 'sge-reportes',
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Reportes</h1></div>
    <div class="card">
      <h2>Reporte de Notas</h2>
      <p>Descargue el reporte de notas en formato Excel (.xlsx)</p>
      <div class="row">
        <div class="field"><label>Curso (opcional)</label>
          <select [(ngModel)]="cursoId">
            <option value="">Todos los cursos</option>
            @for (c of cursos; track c.id) { <option [value]="c.id">{{ c.gradoNombre }} - {{ c.materiaNombre }}</option> }
          </select>
        </div>
        <div class="field"><label>Bimestre (opcional)</label>
          <select [(ngModel)]="bimestreId">
            <option value="">Todos los bimestres</option>
            @for (b of bimestres; track b.id) { <option [value]="b.id">{{ b.nombre }} (#{{ b.numero }})</option> }
          </select>
        </div>
      </div>
      <button class="btn btn-primary" (click)="descargar()" [disabled]="loading">{{ loading ? 'Descargando...' : 'Descargar XLSX' }}</button>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0 0 1rem; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); max-width: 600px; }
    h2 { margin: 0 0 .5rem; font-size: 1.125rem; color: #333; }
    p { color: #666; font-size: .875rem; margin-bottom: 1rem; }
    .row { display: flex; gap: 1rem; margin-bottom: 1rem; }
    .field { display: flex; flex-direction: column; gap: .25rem; flex: 1; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .btn { padding: .625rem 1.25rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
  `]
})
export class Reportes {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  cursos: Curso[] = [];
  bimestres: Bimestre[] = [];
  cursoId = 0;
  bimestreId = 0;
  loading = false;

  ngOnInit() {
    this.api.get<Curso[]>('/cursos').subscribe(r => this.cursos = r);
    this.api.get<PeriodoAcademico[]>('/periodos').subscribe(r => {
      if (r.length > 0) this.api.get<Bimestre[]>('/bimestres', { periodoId: r[0].id! }).subscribe(b => this.bimestres = b);
    });
  }

  descargar() {
    this.loading = true;
    let url = 'http://localhost:8080/api/v1/reportes/notas';
    const params: string[] = [];
    if (this.cursoId) params.push('cursoId=' + this.cursoId);
    if (this.bimestreId) params.push('bimestreId=' + this.bimestreId);
    if (params.length) url += '?' + params.join('&');
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: blob => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'reporte_notas.xlsx';
        a.click();
        URL.revokeObjectURL(a.href);
        this.loading = false;
      },
      error: () => { alert('Error al descargar reporte'); this.loading = false; }
    });
  }
}
