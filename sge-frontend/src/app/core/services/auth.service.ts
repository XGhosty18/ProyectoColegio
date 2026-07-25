import { Injectable, signal } from '@angular/core';
import { ApiService } from './api.service';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/types';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'sge_token';
  private readonly refreshKey = 'sge_refresh';

  readonly isAuthenticated = signal(false);
  readonly username = signal<string | null>(null);
  readonly roles = signal<string[]>([]);

  constructor(private api: ApiService) {
    this.loadFromStorage();
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>('/auth/login', req).pipe(tap(r => this.saveSession(r)));
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>('/auth/register', req).pipe(tap(r => this.saveSession(r)));
  }

  refresh(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem(this.refreshKey);
    return this.api.post<AuthResponse>('/auth/refresh', { refreshToken }).pipe(tap(r => this.saveSession(r)));
  }

  forgotPassword(email: string): Observable<void> {
    return this.api.post('/auth/forgot-password', { email });
  }

  resetPassword(email: string, codigo: string, nuevaPassword: string): Observable<void> {
    return this.api.post('/auth/reset-password', { email, codigo, nuevaPassword });
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshKey);
    this.isAuthenticated.set(false);
    this.username.set(null);
    this.roles.set([]);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  private saveSession(r: AuthResponse): void {
    localStorage.setItem(this.tokenKey, r.token);
    localStorage.setItem(this.refreshKey, r.refreshToken);
    this.isAuthenticated.set(true);
    this.username.set(r.username);
    this.roles.set(r.roles);
  }

  private loadFromStorage(): void {
    const token = localStorage.getItem(this.tokenKey);
    if (token) {
      this.isAuthenticated.set(true);
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.username.set(payload.sub || payload.username);
      this.roles.set(payload.roles || []);
    }
  }
}
