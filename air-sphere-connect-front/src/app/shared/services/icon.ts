import {Injectable} from '@angular/core';
import {type HeroIcon, HEROICONS} from '../icons/heroicons.registry';

@Injectable({
  providedIn: 'root'
})
export class IconService {


  getHeroIcon(name: HeroIcon): string {
    return HEROICONS[name] || '';
  }


  getIconWithClasses(name: HeroIcon, classes: string = ''): string {
    const icon = this.getHeroIcon(name);
    if (!icon) return '';

    return icon
      .replaceAll(/class="[^"]*"/g, '')
      .replace('<svg', `<svg class="${classes}"`);
  }

  getIconWithCustomColor(name: HeroIcon, color: string): string {
    const icon = this.getHeroIcon(name);
    if (!icon) return '';

    return icon.replace(
      'stroke="currentColor"',
      `stroke="var(--color-${color})" style="color: var(--color-${color})"`
    );  }

  getAllHeroIcons(): HeroIcon[] {
    return Object.keys(HEROICONS) as HeroIcon[];
  }

}
