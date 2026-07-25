import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AlertaFaltas } from '../../core/models/types';

@Component({
  selector: 'sge-alertas-faltas-list',
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Alertas de Faltas</h1></div>
    <div class="card">
      <div class="filter">
        <span class="count">{{ list.length }} pendiente(s)</span>
      </div>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Alumno</th><th>Faltas</th><th>Nivel</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          @for (a of list; track a.id) {
            <tr>
              <td>{{ a.id }}</td><td>{{ a.alumnoNombre || '—' }}</td>
              <td>{{ a.cantidadConsecutivas }}</td>
              <td><span class="badge" [class.badge-warn]="a.nivel==='BAJO'" [class.badge-orange]="a.nivel==='MEDIO'" [class.badge-danger]="a.nivel==='ALTO'">{{ a.nivel || '—' }}</span></td>
              <td>{{ a.estado }}</td>
              <td class="actions">
                @if (a.estado === 'NUEVA') { <button class="btn-sm btn-ok" (click)="atender(a)">Atender</button> }
                <button class="btn-sm btn-danger" (click)="eliminar(a)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="6" class="empty">Sin alertas</td></tr> }
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: .75rem 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; }
    .filter { display: flex; gap: .5rem; }
    .btn-sm { padding: .375rem .75rem; border: 1px solid #dadce0; border-radius: 6px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-sm.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }
    .btn-ok { color: #155724; border-color: #c3e6cb; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: .03em; border-bottom: 2px solid #e9ecef; }
    td { padding: .75rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .badge { display: inline-block; padding: .125rem .5rem; border-radius: 99px; font-size: .6875rem; font-weight: 600; }
    .badge-warn { background: #fff3cd; color: #856404; }
    .badge-orange { background: #ffe0b2; color: #e65100; }
    .badge-danger { background: #f8d7da; color: #721c24; }
  `]
})
export class AlertasFaltasList implements OnInit {
  private api = inject(ApiService);
  list: AlertaFaltas[] = [];
  ngOnInit() { this.cargar(); }
  cargar() { this.api.get<AlertaFaltas[]>('/alertas-faltas/pendientes').subscribe(r => this.list = r); }
  atender(a: AlertaFaltas) { this.api.post('/alertas-faltas/' + a.id + '/atender', null).subscribe({ next: () => this.cargar() }); }
  eliminar(a: AlertaFaltas) { if (confirm('¿Eliminar alerta?')) this.api.delete('/alertas-faltas', a.id!).subscribe({ next: () => this.cargar() }); }
}
