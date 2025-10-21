import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../shared/services/UserService';
import { Subscription } from 'rxjs';
import {UserProfileResponse} from '../../core/models/user.model';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class Home implements OnInit, OnDestroy {
  profile: UserProfileResponse | null = null;
  userRole: string | null = null;
  private subscription?: Subscription;

  constructor(private router: Router, private userService: UserService) {}

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
