import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'sge-navbar',
  template: `
    <header class="navbar">
      <span class="brand">SGE</span>
      <span class="user-info">{{ auth.username() }}</span>
      <button class="btn-logout" (click)="logout()">Salir</button>
    </header>
  `,
  styles: [`
    .navbar { display: flex; align-items: center; height: 56px; padding: 0 1.5rem; background: #1a73e8; color: #fff; gap: 1rem; }
    .brand { font-weight: 700; font-size: 1.25rem; }
    .user-info { margin-left: auto; font-size: .875rem; opacity: .9; }
    .btn-logout { background: rgba(255,255,255,.15); color: #fff; border: 1px solid rgba(255,255,255,.3); padding: .375rem .75rem; border-radius: 4px; cursor: pointer; font-size: .8125rem; }
    .btn-logout:hover { background: rgba(255,255,255,.25); }
  `]
})
export class Navbar {
  protected auth = inject(AuthService);
  private router = inject(Router);
  logout() { this.auth.logout(); this.router.navigate(['/login']); }
}
