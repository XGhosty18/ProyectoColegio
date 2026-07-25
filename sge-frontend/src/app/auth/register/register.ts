import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'sge-register',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <form (ngSubmit)="register()" class="auth-card">
        <h1>Crear Cuenta</h1>
        <div class="field">
          <label for="username">Usuario</label>
          <input id="username" name="username" [(ngModel)]="username" required>
        </div>
        <div class="field">
          <label for="email">Email</label>
          <input id="email" name="email" type="email" [(ngModel)]="email" required>
        </div>
        <div class="field">
          <label for="password">Contraseña</label>
          <input id="password" name="password" type="password" [(ngModel)]="password" required>
        </div>
        @if (error) { <div class="error">{{ error }}</div> }
        @if (success) { <div class="success">{{ success }}</div> }
        <button type="submit" [disabled]="loading">Registrarse</button>
        <a routerLink="/login" class="link">Ya tengo cuenta</a>
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
    .error { color: #d93025; font-size: .875rem; margin-bottom: .5rem; text-align: center; }
    .success { color: #188038; font-size: .875rem; margin-bottom: .5rem; text-align: center; }
    .link { text-align: center; margin-top: .75rem; color: #1a73e8; text-decoration: none; font-size: .875rem; }
  `]
})
export class Register {
  private auth = inject(AuthService);
  private router = inject(Router);
  username = ''; email = ''; password = '';
  loading = false; error = ''; success = '';

  register() {
    this.loading = true; this.error = ''; this.success = '';
    this.auth.register({ username: this.username, email: this.email, password: this.password }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: e => { this.error = 'Error al registrarse. Intente de nuevo.'; this.loading = false; }
    });
  }
}
