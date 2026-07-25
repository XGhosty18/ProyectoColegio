import { Component, input, output } from '@angular/core';

@Component({
  selector: 'sge-modal',
  template: `
    @if (open()) {
      <div class="overlay" (click)="close.emit()">
        <div class="modal" (click)="$event.stopPropagation()">
          <div class="header">
            <h2>{{ title() }}</h2>
            <button class="close" (click)="close.emit()">&times;</button>
          </div>
          <div class="body">
            <ng-content></ng-content>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .overlay { position: fixed; inset: 0; background: rgba(0,0,0,.4); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal { background: #fff; border-radius: 10px; width: 100%; max-width: 520px; max-height: 90vh; overflow-y: auto; box-shadow: 0 8px 32px rgba(0,0,0,.2); }
    .header { display: flex; justify-content: space-between; align-items: center; padding: 1rem 1.5rem; border-bottom: 1px solid #e9ecef; }
    .header h2 { margin: 0; font-size: 1.125rem; color: #333; }
    .close { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #999; padding: 0; line-height: 1; }
    .close:hover { color: #333; }
    .body { padding: 1.5rem; }
  `]
})
export class Modal {
  open = input(false);
  title = input('');
  close = output();
}
