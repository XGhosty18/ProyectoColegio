import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  get<T>(path: string, params?: Record<string, string | number | boolean>): Observable<T> {
    let p = new HttpParams();
    if (params) Object.entries(params).forEach(([k, v]) => v !== undefined && (p = p.set(k, v)));
    return this.http.get<T>(`${this.baseUrl}${path}`, { params: p });
  }

  getById<T>(path: string, id: number): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}/${id}`);
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body);
  }

  put<T>(path: string, id: number, body: unknown): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}/${id}`, body);
  }

  putParams<T>(path: string, id: number, params?: Record<string, string | number | boolean>): Observable<T> {
    let p = new HttpParams();
    if (params) Object.entries(params).forEach(([k, v]) => v !== undefined && (p = p.set(k, v)));
    return this.http.put<T>(`${this.baseUrl}${path}/${id}`, null, { params: p });
  }

  delete<T>(path: string, id: number): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}/${id}`);
  }
}
