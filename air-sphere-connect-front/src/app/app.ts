import { RouterOutlet} from '@angular/router';
import {UserService} from './shared/services/user-service';
import {Component, signal, inject, OnInit, NgModule, DestroyRef, effect} from '@angular/core';
import {Header} from './shared/components/layout/header/header';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from './core/interceptors/auth-interceptor';
import {Footer} from './shared/components/layout/footer/footer/footer';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

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

  private readonly destroyRef = inject(DestroyRef);
  private readonly userService = inject(UserService);

  constructor() {
    // User recovery at startup
    this.userService.fetchUserProfile();

    // Listening to profile changes
    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        this.userRole.set(profile?.role ?? 'GUEST');
      });

    effect(() => {
      document.title = this.title();
    });

  }

}
