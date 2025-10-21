import {Router, RouterOutlet} from '@angular/router';
import {UserService} from './shared/services/user-service';
import {Component, signal, inject, OnInit, NgModule} from '@angular/core';
import {ThemeService} from './core/services/theme';
import {Header} from './shared/components/layout/header/header';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from './core/interceptors/auth-interceptor';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})


export class App implements OnInit {
  protected readonly title = signal('AirSphereConnect');

  private readonly themeService = inject(ThemeService);

  userRole = signal<string | null>(null);

  readonly isDarkTheme = this.themeService.isDarkMode;

  constructor(private userService: UserService) {
    this.userService.fetchUserProfile();
    this.userService.userProfile$.subscribe(profile => {
      this.userRole.set(profile?.role ?? 'GUEST');
    });
  }

  @NgModule({
    providers: [
      {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
    ]
  })

  ngOnInit() {
    this.themeService.watchSystemTheme();
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
