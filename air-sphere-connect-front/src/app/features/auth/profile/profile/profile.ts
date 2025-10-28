import {AfterViewInit, ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {UserService} from '../../../../shared/services/user-service';
import {Login} from '../../login/login';
import {User} from '../../../../core/models/user.model';
import {Users} from '../user/users';
import {Tab, TabItem} from '../../../../shared/components/ui/tab/tab';
import {Favorites} from '../favorites/favorites';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterOutlet, Users, Tab, Favorites],
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

  tabs: TabItem[] = [];

  ngAfterViewInit() {
    this.tabs = [
      { label: "Mon profil", template: this.profilUser },
      { label: 'Mes alertes', template: this.favorites },
    ];
    this.cdr.detectChanges();
  }

}
