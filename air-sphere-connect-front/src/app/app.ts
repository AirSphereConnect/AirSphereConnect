import { RouterOutlet} from '@angular/router';
import {UserService} from './shared/services/user-service';
import {
  Component,
  signal,
  inject,
  OnInit,
  NgModule,
  DestroyRef,
  effect,
  ViewChild,
  ElementRef,
  AfterViewInit, OnDestroy
} from '@angular/core';
import {Header} from './shared/components/layout/header/header';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from './core/interceptors/auth-interceptor';
import {Footer} from './shared/components/layout/footer/footer/footer';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {fromEvent, Subscription} from 'rxjs';
import {Forum} from './features/forum/components/forum/forum';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, Forum],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ]
})


export class App implements AfterViewInit, OnDestroy {
  @ViewChild('mainContent') mainContent!: ElementRef<HTMLElement>;

  userRole = signal<string | null>(null);
  scrolled = signal(false);

  protected readonly title = signal('AirSphereConnect');


  private scrollSub!: Subscription;
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

  ngAfterViewInit() {
    this.scrollSub = fromEvent(window, 'scroll')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.scrolled.set(window.scrollY > 0);
      });
  }

  ngOnDestroy() {
    this.scrollSub?.unsubscribe();
  }

}
