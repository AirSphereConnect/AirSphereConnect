import { Injectable, signal, computed, effect } from '@angular/core';

export type Theme = 'airsphere' | 'airspheredark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly STORAGE_KEY = 'airsphere-theme';
  private readonly themeSignal = signal<Theme>('airsphere');

  readonly currentTheme = this.themeSignal.asReadonly();
  readonly isDarkMode = computed(() => this.themeSignal() === 'airspheredark');

  constructor() {
    this.initializeTheme();

    // 🔥 Effect qui s'exécute à chaque changement de thème
    effect(() => {
      const theme = this.themeSignal();
      this.applyTheme(theme);
    });
  }

  private initializeTheme(): void {
    const savedTheme = this.getStoredTheme();
    const initialTheme = savedTheme ?? 'airsphere';
    this.themeSignal.set(initialTheme);
  }

  private applyTheme(theme: Theme): void {
    document.documentElement.dataset["theme"] = theme;
    this.saveTheme(theme);
  }

  private saveTheme(theme: Theme): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, theme);
    } catch (error) {
      this.logError('Erreur lors de la sauvegarde du thème:', error);
    }
  }

  private logError(message: string, error?: unknown): void {
    // Only log errors in development mode
    if (typeof ngDevMode !== 'undefined' && ngDevMode) {
      console.error(message, error);
    }
  }

  private getStoredTheme(): Theme | null {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      return stored === 'airsphere' || stored === 'airspheredark' ? stored : null;
    } catch {
      return null;
    }
  }

  toggleTheme(): void {
    const current = this.themeSignal();
    const next: Theme = current === 'airsphere' ? 'airspheredark' : 'airsphere';
    this.themeSignal.set(next);
  }

  setTheme(theme: Theme): void {
    this.themeSignal.set(theme);
  }
}
