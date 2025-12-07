import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {RouterOutlet, ActivatedRoute} from '@angular/router';
import {UserService} from '../../../../shared/services/user-service';
import {User} from '../../../../core/models/user.model';
import {Tab, TabItem} from '../../../../shared/components/ui/tab/tab';
import {UserDashboard} from '../user/user';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ThreadListComponent} from '../../../forum/components/thread-list/thread-list';
import {Favorites} from '../favorites/favorites';
import {Alerts} from '../alerts/alerts';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterOutlet, UserDashboard, Tab, ThreadListComponent, Favorites, Alerts],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})

export class Profile implements OnInit, AfterViewInit {
  private readonly userService = inject(UserService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);

  user: User | null = null;
  activeTabIndex: number | undefined;

  @ViewChild('profilUser', { static: true }) profilUser!: TemplateRef<unknown>;
  @ViewChild('thread', { static: true }) thread!: TemplateRef<unknown>;
  @ViewChild('favorites', { static: true }) favorites!: TemplateRef<unknown>;
  @ViewChild('alerts', { static: true }) alerts!: TemplateRef<unknown>;


  tabs: TabItem[] = [];

  ngOnInit() {
    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        if (profile && profile.user) {
          this.user = profile.user;
        }
      });

    // Lire le query param "tab" pour ouvrir l'onglet correspondant
    this.route.queryParams
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(params => {
        const tabParam = params['tab'];
        if (tabParam) {
          const tabIndex = this.getTabIndex(tabParam);
          if (tabIndex !== -1) {
            this.activeTabIndex = tabIndex;
          }
        }
      });
  }

  private getTabIndex(tabName: string): number {
    const tabMap: Record<string, number> = {
      'profile': 0,
      'threads': 1,
      'favorites': 2,
      'alerts': 3
    };
    return tabMap[tabName] ?? -1;
  }

  ngAfterViewInit() {
    this.tabs = [
      { label: "Mon profil", template: this.profilUser },
      { label: "Mes rubriques", template: this.thread },
      { label: 'Mes favoris', template: this.favorites },
      { label: 'Mes alertes', template: this.alerts },
    ];
    this.cdr.detectChanges();
  }

}
