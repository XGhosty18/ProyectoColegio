import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Documento } from '../../core/models/types';
import { Modal } from '../../shared/modal';

@Component({
  selector: 'sge-documentos-list',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-header"><h1>Documentos</h1></div>
    <div class="card">
      <div class="row-fields">
        <div class="field"><label>Tipo Entidad</label>
          <select [(ngModel)]="entidadTipo" (change)="cargar()">
            <option value="ALUMNO">Alumno</option><option value="DOCENTE">Docente</option><option value="CURSO">Curso</option>
            <option value="PAGO">Pago</option><option value="PADRE">Padre</option>
          </select>
        </div>
        <div class="field"><label>ID Entidad</label><input type="number" [(ngModel)]="entidadId" (change)="cargar()" placeholder="ID"></div>
        <button class="btn btn-primary" (click)="mostrarSubir()" [disabled]="!entidadId">Subir</button>
      </div>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Nombre</th><th>Tipo</th><th>MIME</th><th></th></tr></thead>
        <tbody>
          @for (d of list; track d.id) {
            <tr>
              <td>{{ d.id }}</td><td>{{ d.nombreArchivo }}</td><td>{{ d.tipoDoc }}</td><td>{{ d.mimeType || '—' }}</td>
              <td class="actions">
                <button class="btn-sm" (click)="subirVersion(d)">Actualizar</button>
                <button class="btn-sm btn-danger" (click)="eliminar(d)">Eliminar</button>
              </td>
            </tr>
          } @empty { <tr><td colspan="5" class="empty">{{ entidadId ? 'Sin documentos' : 'Seleccione entidad e ID' }}</td></tr> }
        </tbody>
      </table>
    </div>
    <sge-modal [open]="showForm()" [title]="'Subir Documento'" (close)="cerrarForm()">
      <form (ngSubmit)="subir()" class="form">
        <div class="field"><label>Tipo Documento</label>
          <select [(ngModel)]="tipoDoc" name="tipoDoc" required>
            <option value="DNI">DNI</option><option value="ACTA">Acta</option><option value="CERTIFICADO">Certificado</option>
            <option value="COMPROBANTE">Comprobante</option><option value="OTRO">Otro</option>
          </select>
        </div>
        <div class="field"><label>Archivo</label><input type="file" (change)="onFileChange($event)" accept=".pdf,.jpg,.png,.doc,.docx"></div>
        <div class="form-actions">
          <button type="button" class="btn btn-cancel" (click)="cerrarForm()">Cancelar</button>
          <button type="submit" class="btn btn-primary" [disabled]="saving || !archivo">{{ saving ? 'Subiendo...' : 'Subir' }}</button>
        </div>
      </form>
    </sge-modal>
  `,
  styles: [`
    .page-header { margin-bottom: 1rem; }
    h1 { margin: 0; font-size: 1.5rem; color: #333; }
    .card { background: #fff; border-radius: 8px; padding: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); margin-bottom: 1rem; }
    .row-fields { display: flex; gap: .75rem; align-items: flex-end; }
    .field { display: flex; flex-direction: column; gap: .25rem; flex: 1; }
    label { font-size: .875rem; color: #333; font-weight: 500; }
    select, input { padding: .5rem .75rem; border: 1px solid #dadce0; border-radius: 6px; font-size: .875rem; }
    .table-wrap { background: #fff; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,.08); overflow: auto; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8f9fa; text-align: left; padding: .75rem 1rem; font-size: .8125rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: .03em; border-bottom: 2px solid #e9ecef; }
    td { padding: .75rem 1rem; font-size: .875rem; color: #333; border-bottom: 1px solid #f0f0f0; }
    .empty { text-align: center; color: #999; padding: 2rem; }
    .actions { display: flex; gap: .375rem; }
    .btn { padding: .5rem 1rem; border: none; border-radius: 6px; cursor: pointer; font-size: .875rem; white-space: nowrap; }
    .btn-primary { background: #1a73e8; color: #fff; }
    .btn-cancel { background: #f1f3f4; color: #333; }
    .btn:disabled { opacity: .6; cursor: not-allowed; }
    .btn-sm { padding: .25rem .625rem; border: 1px solid #dadce0; border-radius: 4px; background: #fff; cursor: pointer; font-size: .8125rem; }
    .btn-danger { color: #d93025; border-color: #f5c6cb; }
    .form { display: flex; flex-direction: column; gap: .75rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: .5rem; margin-top: .5rem; }
  `]
})
export class DocumentosList implements OnInit {
  private api = inject(ApiService);
  list: Documento[] = [];
  entidadTipo = 'ALUMNO';
  entidadId = 0;
  showForm = signal(false);
  saving = false;
  archivo?: File;
  tipoDoc = 'DNI';
  editDocId?: number;
  ngOnInit() {}
  cargar() {
    if (!this.entidadId) { this.list = []; return; }
    this.api.get<Documento[]>('/documentos/' + this.entidadTipo + '/' + this.entidadId).subscribe(r => this.list = r);
  }
  mostrarSubir() { this.editDocId = undefined; this.tipoDoc = 'DNI'; this.archivo = undefined; this.showForm.set(true); }
  subirVersion(d: Documento) { this.editDocId = d.id; this.tipoDoc = d.tipoDoc; this.archivo = undefined; this.showForm.set(true); }
  cerrarForm() { this.showForm.set(false); }
  onFileChange(e: Event) {
    const input = e.currentTarget as HTMLInputElement;
    if (input.files?.length) this.archivo = input.files[0];
  }
  subir() {
    if (!this.archivo || !this.entidadId) return;
    this.saving = true;
    const fd = new FormData();
    fd.append('file', this.archivo);
    fd.append('tipoDoc', this.tipoDoc);
    if (this.editDocId) {
      this.api.put<Documento>('/documentos/' + this.editDocId + '?tipoDoc=' + this.tipoDoc, this.editDocId, fd).subscribe({
        next: () => { this.cargar(); this.cerrarForm(); this.saving = false; },
        error: () => { alert('Error'); this.saving = false; }
      });
    } else {
      fd.append('entidadTipo', this.entidadTipo);
      fd.append('entidadId', String(this.entidadId));
      this.api.post<Documento>('/documentos', fd).subscribe({
        next: () => { this.cargar(); this.cerrarForm(); this.saving = false; },
        error: () => { alert('Error'); this.saving = false; }
      });
    }
  }
  eliminar(d: Documento) { if (confirm('¿Eliminar documento?')) this.api.delete('/documentos', d.id!).subscribe({ next: () => this.cargar() }); }
}
