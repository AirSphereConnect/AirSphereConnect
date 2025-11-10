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
import {RouterOutlet} from '@angular/router';
import {UserService} from '../../../../shared/services/user-service';
import {User} from '../../../../core/models/user.model';
import {Tab, TabItem} from '../../../../shared/components/ui/tab/tab';
import {UserDashboard} from '../user/users';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ThreadListComponent} from '../../../forum/components/thread-list/thread-list';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterOutlet, UserDashboard, Tab, ThreadListComponent],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})

export class Profile implements OnInit, AfterViewInit {
  private readonly userService = inject(UserService);
  private readonly cdr = inject(ChangeDetectorRef);

  private readonly destroyRef = inject(DestroyRef);
  user: User | null = null;

  @ViewChild('profilUser', { static: true }) profilUser!: TemplateRef<unknown>;
  @ViewChild('thread', { static: true }) thread!: TemplateRef<unknown>;
  @ViewChild('favorites', { static: true }) favorites!: TemplateRef<unknown>;
  @ViewChild('alerts', { static: true }) alerts!: TemplateRef<unknown>;


  tabs: TabItem[] = [];

  ngOnInit() {
    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        if (profile) {
          this.user = profile.user;
        }
      });
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
