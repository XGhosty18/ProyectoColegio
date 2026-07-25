import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'sge-forgot-password',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <form (ngSubmit)="submit()" class="auth-card">
        <h1>Recuperar Contraseña</h1>
        @if (!codigoEnviado) {
          <div class="field">
            <label for="email">Email</label>
            <input id="email" name="email" type="email" [(ngModel)]="email" required>
          </div>
          @if (error) { <div class="error">{{ error }}</div> }
          @if (success) { <div class="success">{{ success }}</div> }
          <button type="submit" [disabled]="loading">Enviar código</button>
        } @else {
          <p class="info">Se ha enviado un código a tu email. Revisa la consola del backend.</p>
          <div class="field">
            <label for="codigo">Código</label>
            <input id="codigo" name="codigo" [(ngModel)]="codigo" required>
          </div>
          <div class="field">
            <label for="nuevaPassword">Nueva contraseña</label>
            <input id="nuevaPassword" name="nuevaPassword" type="password" [(ngModel)]="nuevaPassword" required>
          </div>
          @if (error) { <div class="error">{{ error }}</div> }
          <button type="submit" [disabled]="loading">Restablecer</button>
        }
        <a routerLink="/login" class="link">Volver al login</a>
      </form>
    </div>
  `,
  styles: [`
    .auth-container { display: flex; align-items: center; justify-content: center; min-height: 100vh; background: #f0f2f5; }
    .auth-card { background: #fff; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,.1); width: 100%; max-width: 400px; display: flex; flex-direction: column; }
    h1 { text-align: center; color: #333; font-size: 1.5rem; margin: 0 0 1.5rem; }
    .field { display: flex; flex-direction: column; gap: .25rem; margin-bottom: 1rem; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    input { padding: .625rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    input:focus { outline: none; border-color: #1a73e8; box-shadow: 0 0 0 2px rgba(26,115,232,.2); }
    button { padding: .75rem; background: #1a73e8; color: #fff; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; margin-top: .5rem; }
    button:disabled { opacity: .6; cursor: not-allowed; }
    .info { font-size: .875rem; color: #666; margin-bottom: 1rem; text-align: center; }
    .error { color: #d93025; font-size: .875rem; margin-bottom: .5rem; text-align: center; }
    .success { color: #188038; font-size: .875rem; margin-bottom: .5rem; text-align: center; }
    .link { text-align: center; margin-top: .75rem; color: #1a73e8; text-decoration: none; font-size: .875rem; }
  `]
})
export class ForgotPassword {
  private auth = inject(AuthService);
  email = ''; codigo = ''; nuevaPassword = '';
  loading = false; error = ''; success = '';
  codigoEnviado = false;

  submit() {
    this.loading = true; this.error = ''; this.success = '';
    if (!this.codigoEnviado) {
      this.auth.forgotPassword(this.email).subscribe({
        next: () => { this.codigoEnviado = true; this.success = 'Código enviado. Revisa la consola del backend.'; this.loading = false; },
        error: () => { this.error = 'Email no encontrado'; this.loading = false; }
      });
    } else {
      this.auth.resetPassword(this.email, this.codigo, this.nuevaPassword).subscribe({
        next: () => { this.success = 'Contraseña restablecida. Redirigiendo...'; this.loading = false; },
        error: () => { this.error = 'Código inválido o expirado'; this.loading = false; }
      });
    }
  }
}
