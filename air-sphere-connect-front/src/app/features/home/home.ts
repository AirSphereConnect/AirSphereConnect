import {Component, inject, Input, OnDestroy, OnInit} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../shared/services/user-service';
import { Subscription } from 'rxjs';
import {UserProfileResponse} from '../../core/models/user.model';
import {Button} from '../../shared/components/ui/button/button';

@Component({
  selector: 'app-home',
  imports: [RouterLink, Button],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class Home implements OnInit, OnDestroy {
  profile: UserProfileResponse | null = null;
  @Input() userRole: string | null = null;
  private subscription?: Subscription;
  private readonly router = inject(Router);
  private readonly userService = inject(UserService);


  ngOnInit() {
    this.subscription = this.userService.userProfile$.subscribe(profile => {
      this.profile = profile;
      this.userRole = profile?.role ?? 'GUEST';
    });
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }
}
