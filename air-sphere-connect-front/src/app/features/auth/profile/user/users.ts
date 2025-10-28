import {Component, Input} from '@angular/core';
import {User} from '../../../../core/models/user.model';
import {Button} from '../../../../shared/components/ui/button/button';
import {Router} from '@angular/router';


@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [Button],
  templateUrl: './user.html',
  styleUrls: ['./user.scss']
})
export class Users {
  @Input() user!: User | null;
  constructor(private router: Router) {}

  onEditUser() {
    this.router.navigate(['/auth/profile/user/edit']);
  }
}

