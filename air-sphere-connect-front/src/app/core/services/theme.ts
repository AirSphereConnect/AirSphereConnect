// src/app/core/services/theme.service.ts
import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'airsphere' | 'airspheredark';

@Injectable({
  providedIn: 'root'
})

export class ThemeService {
  private readonly themeSignal = signal<Theme>('airsphere');

  readonly isDarkMode = signal(false);

  readonly currentTheme = this.themeSignal.asReadonly();

  private readonly STORAGE_KEY = 'airsphere-theme';

  constructor() {
    this.initializeTheme();

    effect(() => {
      const theme = this.themeSignal();
      this.applyTheme(theme);
      this.isDarkMode.set(theme === 'airspheredark');
    });
  }

  private initializeTheme(): void {
    const savedTheme = this.getStoredTheme();

    if (savedTheme) {
      this.themeSignal.set(savedTheme);
      return;
    }

    this.themeSignal.set('airsphere');

    // const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    // this.themeSignal.set(prefersDark ? 'airspheredark' : 'airsphere');
  }


  private applyTheme(theme: Theme): void {
    document.documentElement.setAttribute('data-theme', theme);

    this.saveTheme(theme);

    console.log(`🎨 Thème appliqué: ${theme}`);
  }

  private saveTheme(theme: Theme): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, theme);
    } catch (error) {
      console.error('Erreur lors de la sauvegarde du thème:', error);
    }
  }

  private getStoredTheme(): Theme | null {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      return stored === 'airsphere' || stored === 'airspheredark' ? stored : null;
    } catch (error) {
      console.error('Erreur lors de la lecture du thème:', error);
      return null;
    }
  }

  toggleTheme(): void {
    const newTheme: Theme = this.isDarkMode() ? 'airsphere' : 'airspheredark';
    this.themeSignal.set(newTheme);
  }

  setTheme(theme: Theme): void {
    this.themeSignal.set(theme);
  }

  setLightMode(): void {
    this.setTheme('airsphere');
  }


  setDarkMode(): void {
    this.setTheme('airspheredark');
  }


  watchSystemTheme(): void {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');

    mediaQuery.addEventListener('change', (e) => {
      // Ne changer que si l'utilisateur n'a pas de préférence sauvegardée
      if (!this.getStoredTheme()) {
        this.themeSignal.set(e.matches ? 'airspheredark' : 'airsphere');
      }
    });
  }
}
