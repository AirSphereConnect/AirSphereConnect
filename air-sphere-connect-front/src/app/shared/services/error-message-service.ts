import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorMessageService {
  message = signal<string | null>(null);

  setMessage(msg: string, duration = 1000) {
    this.message.set(msg);
    setTimeout(() => {
      this.message.set(null);
    }, duration);
  }
}
