import { RouterOutlet} from '@angular/router';
import {UserService} from './shared/services/user-service';
import {
  Component,
  signal,
  inject,
  DestroyRef,
  effect,
  ViewChild,
  ElementRef,
  AfterViewInit, OnDestroy
} from '@angular/core';
import {Header} from './shared/components/layout/header/header';
import {Footer} from './shared/components/layout/footer/footer/footer';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {fromEvent, Subscription} from 'rxjs';
import {ErrorMessage} from './shared/components/ui/error-message/error-message';
import {BackToTop} from './shared/components/ui/back-to-top/back-to-top';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, ErrorMessage, BackToTop, ErrorMessage],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})


export class App implements AfterViewInit, OnDestroy {
  @ViewChild('mainContent') mainContent!: ElementRef<HTMLElement>;

  userRole = signal<string | null>(null);
  scrolled = signal(false);

  protected readonly title = signal('AirSphereConnect');


  private scrollSub!: Subscription;
  private scrollSubTop!: Subscription;
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
    this.scrollSub = fromEvent(globalThis, 'scroll')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.scrolled.set(window.scrollY > 0);
      });

    this.scrollSubTop = fromEvent(window, 'scroll')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.scrolled.set(window.scrollY > 50);
      });
  }

  ngOnDestroy() {
    this.scrollSub?.unsubscribe();
    this.scrollSubTop?.unsubscribe();
  }

  top() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
