import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/services/api.service';
import { AuthService } from '../core/services/auth.service';
import { StripePaymentIntentRequest, CronogramaPago, Alumno, Usuario } from '../core/models/types';

@Component({
  selector: 'sge-stripe-payment',
  imports: [FormsModule],
  template: `
    <div class="page-header"><h1>Pago con Tarjeta (Stripe)</h1></div>
    <div class="card">
      <div class="field"><label>Alumno</label>
        <select [(ngModel)]="alumnoId" (change)="cargarCronogramas()">
          <option value="">Seleccione alumno...</option>
          @for (a of alumnos; track a.id) { <option [value]="a.id">{{ a.nombres }} {{ a.apellidos }} ({{ a.codigoEstudiante }})</option> }
        </select>
      </div>
      @if (cronogramas().length > 0) {
        <div class="field"><label>Cronograma de Pago</label>
          <select [(ngModel)]="cronogramaId">
            <option value="">Seleccione...</option>
            @for (c of cronogramas(); track c.id) {
              <option [value]="c.id">{{ c.conceptoNombre }} — vence {{ c.fechaVencimiento }} — S/ {{ (c.monto ?? 0).toFixed(2) }}</option>
            }
          </select>
        </div>
        <button class="btn btn-primary" (click)="pagar()" [disabled]="loading || !cronogramaId || !usuarioId">
          {{ loading ? 'Procesando...' : 'Pagar con Stripe' }}
        </button>
      } @else if (alumnoId) { <p class="empty">No hay pagos pendientes para este alumno</p> }
      @if (clientSecret()) {
        <div class="info">
          <p>PaymentIntent creado exitosamente.</p>
          <p class="note">En producción, aquí se integraría Stripe Elements para ingresar los datos de la tarjeta.</p>
          <p class="note">ClientSecret: <code>{{ clientSecret() }}</code></p>
        </div>
      }
      @if (error()) { <div class="error">{{ error() }}</div> }
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0 0 1rem; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); max-width: 500px; }
    .field { display: flex; flex-direction: column; gap: .25rem; margin-bottom: 1rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .btn { padding: .625rem 1.25rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    .empty { color: #999; font-size: .875rem; }
    .info { margin-top: 1rem; padding: .75rem; background: #e8f5e9; border-radius: 6px; font-size: .8125rem; }
    .info code { font-size: .75rem; word-break: break-all; }
    .note { margin-top: .25rem; color: #666; font-style: italic; }
    .error { margin-top: 1rem; color: #d93025; font-size: .875rem; }
  `]
})
export class StripePayment {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  alumnos: Alumno[] = [];
  usuarioId = 0;
  alumnoId = 0;
  cronogramaId = 0;
  loading = false;
  cronogramas = signal<CronogramaPago[]>([]);
  clientSecret = signal<string | null>(null);
  error = signal<string | null>(null);

  ngOnInit() {
    this.api.get<Alumno[]>('/alumnos').subscribe(r => this.alumnos = r);
    this.api.get<Usuario[]>('/usuarios').subscribe(usuarios => {
      const current = usuarios.find(u => u.username === this.auth.username());
      if (current?.id) this.usuarioId = current.id;
    });
  }

  cargarCronogramas() {
    if (!this.alumnoId) return;
    this.api.get<CronogramaPago[]>('/cronograma-pagos/alumno/' + this.alumnoId).subscribe({
      next: r => this.cronogramas.set(r.filter(c => c.estado === 'PENDIENTE')),
      error: () => this.cronogramas.set([])
    });
  }

  pagar() {
    if (!this.cronogramaId || !this.usuarioId) return;
    this.loading = true; this.error.set(null); this.clientSecret.set(null);
    this.api.post<{clientSecret: string; paymentIntentId: string; amount: number; currency: string}>('/stripe/create-payment-intent', {
      cronogramaPagoId: this.cronogramaId,
      alumnoId: this.alumnoId,
      usuarioId: this.usuarioId
    }).subscribe({
      next: r => { this.clientSecret.set(r.clientSecret); this.loading = false; },
      error: () => { this.error.set('Error al crear el pago'); this.loading = false; }
    });
  }
}
