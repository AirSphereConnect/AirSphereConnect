import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'app-modals',
  template: `
    <div class="fixed inset-0 flex items-center bg-white/5 backdrop-blur-sm justify-center z-50"
         (click)="onBackdropClick()">
      <div class="bg-base-100 shadow-2xl p-6 rounded-2xl w-full max-w-md relative"
           (click)="$event.stopPropagation()">
        <ng-content></ng-content>
      </div>
    </div>
`,
})
export class Modal {
  @Output() close = new EventEmitter<void>();

  onBackdropClick() {
    this.close.emit();
  }
}
