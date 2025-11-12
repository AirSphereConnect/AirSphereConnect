import { Injectable } from '@angular/core';

// Déclaration pour accéder à window.__env
declare global {
  interface Window {
    __env?: {
      apiUrl?: string;
    };
  }
}

@Injectable({
  providedIn: 'root'
})
export class ApiConfigService {
  readonly apiUrl: string;

  constructor() {
    // Lecture depuis le fichier env.js injecté au runtime
    // Fallback sur localhost pour le développement local (sans Docker)
    this.apiUrl = window.__env?.apiUrl || 'http://localhost:8080/api';
  }
}
