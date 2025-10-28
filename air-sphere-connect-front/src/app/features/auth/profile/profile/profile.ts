import {AfterViewInit, ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {UserService} from '../../../../shared/services/user-service';
import {User} from '../../../../core/models/user.model';
import {Tab, TabItem} from '../../../../shared/components/ui/tab/tab';
import {Favorites} from '../favorites/favorites';
import {Alerts} from '../alerts/alerts';
import {UserDashboard} from '../user/users';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterOutlet, UserDashboard, Tab, Favorites, Alerts],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})

export class Profile implements OnInit, AfterViewInit {

  user: User | null = null;

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.userService.userProfile$
      .subscribe(profile => {
        if (profile) {
          this.user = profile.user;
        }
      });
  }

  @ViewChild('profilUser', { static: true }) profilUser!: TemplateRef<unknown>;
  @ViewChild('favorites', { static: true }) favorites!: TemplateRef<unknown>;
  @ViewChild('alerts', { static: true }) alerts!: TemplateRef<unknown>;

  tabs: TabItem[] = [];

  ngAfterViewInit() {
    this.tabs = [
      { label: "Mon profil", template: this.profilUser },
      { label: 'Mes favoris', template: this.favorites },
      { label: 'Mes alertes', template: this.alerts },
    ];
    this.cdr.detectChanges();
  }

}
