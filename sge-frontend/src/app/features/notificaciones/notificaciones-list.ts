import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { Notificacion, Usuario } from '../../core/models/types';

@Component({
  selector: 'sge-notificaciones-list',
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Notificaciones</h1></div>
    <div class="card">
      <div class="filter">
        <button class="btn btn-sm" [class.active]="!soloPendientes" (click)="soloPendientes=false; cargar()">Todas</button>
        <button class="btn btn-sm" [class.active]="soloPendientes" (click)="soloPendientes=true; cargar()">Pendientes</button>
        @if (pendientesCount > 0) { <span class="count">{{ pendientesCount }} sin leer</span> }
      </div>
    </div>
    <div class="list">
      @for (n of list; track n.id) {
        <div class="notif" [class.unread]="!n.leida" (click)="marcarLeida(n)">
          <div class="notif-header">
            <span class="badge" [class.badge-info]="n.tipo==='INFO'" [class.badge-warn]="n.tipo==='ALERTA'" [class.badge-ok]="n.tipo==='EXITO'">{{ n.tipo }}</span>
            <span class="date">{{ formatDate(n.createdAt) }}</span>
          </div>
          <div class="notif-title">{{ n.titulo }}</div>
          <div class="notif-body">{{ n.cuerpo }}</div>
          <div class="notif-footer">
            <span class="status">{{ n.leida ? '✓ Leída' : '● No leída' }}</span>
          </div>
        </div>
      } @empty {
        <div class="empty-state">No hay notificaciones</div>
      }
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: .75rem 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; }
    .filter { display: flex; align-items: center; gap: .5rem; }
    .btn-sm { padding: .375rem .75rem; border: 1px solid #dadce0; border-radius: 6px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-sm.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }
    .count { margin-left: auto; font-size: .8125rem; color: #d93025; font-weight: 600; }
    .list { display: flex; flex-direction: column; gap: .5rem; }
    .notif { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); cursor: pointer; transition: all .15s; border-left: 3px solid transparent; }
    .notif:hover { box-shadow: 0 2px 6px rgba(0,0,0,.12); }
    .notif.unread { border-left-color: #1a73e8; background: #f8faff; }
    .notif-header { display: flex; align-items: center; gap: .5rem; margin-bottom: .375rem; }
    .badge { display: inline-block; padding: .125rem .5rem; border-radius: 99px; font-size: .6875rem; font-weight: 600; text-transform: uppercase; }
    .badge-info { background: #e8f0fe; color: #1a73e8; }
    .badge-warn { background: #fff3cd; color: #856404; }
    .badge-ok { background: #d4edda; color: #155724; }
    .date { font-size: .75rem; color: #999; margin-left: auto; }
    .notif-title { font-size: .9375rem; font-weight: 600; color: #333; margin-bottom: .25rem; }
    .notif-body { font-size: .875rem; color: #666; line-height: 1.4; }
    .notif-footer { margin-top: .5rem; }
    .status { font-size: .75rem; color: #999; }
    .empty-state { text-align: center; color: #999; padding: 3rem; background: #fff; border-radius: 8px; }
  `]
})
export class NotificacionesList implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  list: Notificacion[] = [];
  usuarios: Usuario[] = [];
  userId = 0;
  soloPendientes = false;
  pendientesCount = 0;

  ngOnInit() {
    this.api.get<Usuario[]>('/usuarios').subscribe(usuarios => {
      this.usuarios = usuarios;
      const current = usuarios.find(u => u.username === this.auth.username());
      if (current?.id) { this.userId = current.id; this.cargar(); }
    });
  }
  cargar() {
    if (!this.userId) return;
    const endpoint = this.soloPendientes ? '/notificaciones/pendientes/' : '/notificaciones/usuario/';
    this.api.get<Notificacion[]>(endpoint + this.userId).subscribe(r => this.list = r);
    this.api.get<Notificacion[]>('/notificaciones/pendientes/' + this.userId).subscribe(r => this.pendientesCount = r.length);
  }
  marcarLeida(n: Notificacion) {
    if (n.leida) return;
    this.api.post('/notificaciones/' + n.id + '/leer', null).subscribe({
      next: () => { n.leida = true; this.pendientesCount = Math.max(0, this.pendientesCount - 1); },
      error: () => {}
    });
  }
  formatDate(iso?: string): string {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleDateString('es-PE', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });
  }
}
