import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {UserService} from './shared/services/user-service';
import {
  Component,
  signal,
  inject,
  DestroyRef,
  effect,
  ViewChild,
  ElementRef,
  AfterViewInit, OnDestroy, computed, OnInit
} from '@angular/core';
import {Header} from './shared/components/layout/header/header';
import {Footer} from './shared/components/layout/footer/footer/footer';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {filter, fromEvent, Subscription} from 'rxjs';
import {BackToTop} from './shared/components/ui/back-to-top/back-to-top';
import {Notification} from './shared/components/ui/notification/notification';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, BackToTop, Notification],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})


export class App implements AfterViewInit, OnDestroy, OnInit {
  @ViewChild('mainContent') mainContent!: ElementRef<HTMLElement>;

  userRole = signal<string | null>(null);
  scrolled = signal(false);

  protected readonly title = signal('AirSphereConnect');
  protected readonly router = inject(Router)

  private scrollSub!: Subscription;
  private scrollSubTop!: Subscription;
  private readonly destroyRef = inject(DestroyRef);
  private readonly userService = inject(UserService);

  // Update document title when title signal changes
  private readonly titleEffect = effect(() => {
    document.title = this.title();
  });

  ngOnInit() {
    // User recovery at startup
    this.userService.fetchUserProfile();

    // Listening to profile changes
    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        this.userRole.set(profile?.role ?? 'GUEST');
      });
  }

  ngAfterViewInit() {
    this.scrollSub = fromEvent(globalThis, 'scroll')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.scrolled.set(window.scrollY > 0);
      });

    this.scrollSubTop = fromEvent(globalThis, 'scroll')
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
