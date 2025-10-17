import { Component, OnInit } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import { User, UserService} from '../../../shared/services/UserService';
import {Login} from '../login/login'; // Ajuste les imports

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterOutlet, Login],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})
export class Profile implements OnInit {

  user: User | null = null;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.userProfile$
      .subscribe(profile => {
        if (profile) {
          this.user = profile.user;
        }
      });
  }


}
