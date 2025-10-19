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

  /**
   * 🎬 Initialiser le thème au chargement
   */
  private initializeTheme(): void {
    const savedTheme = this.getStoredTheme();

    if (savedTheme) {
      this.themeSignal.set(savedTheme);
      return;
    }

    this.themeSignal.set('airsphere');  // ← Force toujours light au début

    // ⚠️ Si vous voulez suivre les préférences système, décommentez :
    // const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    // this.themeSignal.set(prefersDark ? 'airspheredark' : 'airsphere');
  }

  /**
   * 🎨 Appliquer le thème au DOM
   */
  private applyTheme(theme: Theme): void {
    // Appliquer l'attribut data-theme sur le HTML
    document.documentElement.setAttribute('data-theme', theme);

    // Sauvegarder dans localStorage
    this.saveTheme(theme);

    // Logger pour debug (optionnel)
    console.log(`🎨 Thème appliqué: ${theme}`);
  }

  /**
   * 💾 Sauvegarder le thème dans localStorage
   */
  private saveTheme(theme: Theme): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, theme);
    } catch (error) {
      console.error('Erreur lors de la sauvegarde du thème:', error);
    }
  }

  /**
   * 📖 Lire le thème depuis localStorage
   */
  private getStoredTheme(): Theme | null {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      return stored === 'airsphere' || stored === 'airspheredark' ? stored : null;
    } catch (error) {
      console.error('Erreur lors de la lecture du thème:', error);
      return null;
    }
  }

  /**
   * 🔄 Toggle entre light et dark
   */
  toggleTheme(): void {
    const newTheme: Theme = this.isDarkMode() ? 'airsphere' : 'airspheredark';
    this.themeSignal.set(newTheme);
  }

  /**
   * 🎯 Définir un thème spécifique
   */
  setTheme(theme: Theme): void {
    this.themeSignal.set(theme);
  }

  /**
   * 🌞 Passer en mode light
   */
  setLightMode(): void {
    this.setTheme('airsphere');
  }

  /**
   * 🌙 Passer en mode dark
   */
  setDarkMode(): void {
    this.setTheme('airspheredark');
  }

  /**
   * 🔍 Observer les changements de préférence système (OPTIONNEL)
   */
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
