import {Router, RouterOutlet} from '@angular/router';
import {UserService} from './shared/services/user-service';
import {Component, signal, inject, OnInit, NgModule} from '@angular/core';
import {ThemeService} from './core/services/theme';
import {Header} from './shared/components/layout/header/header';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from './core/interceptors/auth-interceptor';
import {Footer} from './shared/components/layout/footer/footer/footer';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ]
})


export class App {
  protected readonly title = signal('AirSphereConnect');

  userRole = signal<string | null>(null);

  constructor(private userService: UserService) {
    this.userService.fetchUserProfile();
    this.userService.userProfile$.subscribe(profile => {
      this.userRole.set(profile?.role ?? 'GUEST');
    });
  }

}
