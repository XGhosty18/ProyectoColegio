import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../core/services/auth.service';
import { ApiService } from '../core/services/api.service';

@Component({
  selector: 'sge-dashboard',
  template: `
    <h1>Dashboard</h1>
    <p>Bienvenido, {{ auth.username() }}</p>
    <div class="cards">
      <div class="card"><span class="num">{{ alumnos }}</span><span class="lbl">Alumnos</span></div>
      <div class="card"><span class="num">{{ docentes }}</span><span class="lbl">Docentes</span></div>
      <div class="card"><span class="num">{{ cursos }}</span><span class="lbl">Cursos</span></div>
      <div class="card"><span class="num">{{ pagosPendientes }}</span><span class="lbl">Pagos Pendientes</span></div>
    </div>
  `,
  styles: [`
    h1 { margin: 0 0 .5rem; font-size: 1.5rem; color: #333; }
    p { color: #666; margin-bottom: 1.5rem; }
    .cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem; }
    .card { background: #fff; border-radius: 8px; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); display: flex; flex-direction: column; gap: .25rem; }
    .num { font-size: 2rem; font-weight: 700; color: #1a73e8; }
    .lbl { font-size: .875rem; color: #666; }
  `]
})
export class Dashboard implements OnInit {
  protected auth = inject(AuthService);
  private api = inject(ApiService);
  alumnos = 0; docentes = 0; cursos = 0; pagosPendientes = 0;

  ngOnInit() {
    this.api.get<any[]>('/alumnos').subscribe(r => this.alumnos = r.length);
    this.api.get<any[]>('/docentes').subscribe(r => this.docentes = r.length);
    this.api.get<any[]>('/cursos').subscribe(r => this.cursos = r.length);
    this.api.get<number>('/cronograma-pagos/count/pendientes').subscribe(r => this.pagosPendientes = r);
  }
}
