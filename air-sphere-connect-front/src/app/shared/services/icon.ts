import {Injectable} from '@angular/core';
import {type HeroIconName, HEROICONS} from '../icons/heroicons.registry';

@Injectable({
  providedIn: 'root'
})
export class IconService {


  getHeroIcon(name: HeroIconName): string {
    return HEROICONS[name] || '';
  }


  getIconWithClasses(name: HeroIconName, classes: string = ''): string {
    const icon = this.getHeroIcon(name);
    if (!icon) return '';

    return icon
      .replace(/class="[^"]*"/g, '')
      .replace('<svg', `<svg class="${classes}"`);
  }

  getIconWithCustomColor(name: HeroIconName, color: string): string {
    const icon = this.getHeroIcon(name);
    if (!icon) return '';

    return icon.replace(
      'stroke="currentColor"',
      `stroke="var(--color-${color})" style="color: var(--color-${color})"`
    );  }

  getAllHeroIconNames(): HeroIconName[] {
    return Object.keys(HEROICONS) as HeroIconName[];
  }

}
