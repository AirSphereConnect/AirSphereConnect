import {Router, RouterOutlet} from '@angular/router';
import {UserService} from './shared/services/user-service';
import {
  AfterViewInit,
  Component,
  DestroyRef,
  effect,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewChild
} from '@angular/core';
import {Header} from './shared/components/layout/header/header';
import {Footer} from './shared/components/layout/footer/footer/footer';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {fromEvent, Subscription} from 'rxjs';
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
  private readonly destroyRef = inject(DestroyRef);
  private readonly userService = inject(UserService);

  // Update document title when title signal changes
  private readonly titleEffect = effect(() => {
    document.title = this.title();
  });

  splashDone = false;

  ngOnInit() {
    setTimeout(() => {
      this.splashDone = true;
    }, 1500);

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
        this.scrolled.set(window.scrollY > 50);
      });
  }

  ngOnDestroy() {
    this.scrollSub?.unsubscribe();
  }

  top() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
