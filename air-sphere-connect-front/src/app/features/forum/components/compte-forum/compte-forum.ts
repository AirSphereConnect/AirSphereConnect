import {Component, inject, OnInit, signal} from '@angular/core';
import {ThreadService} from '../../../../core/services/thread.service';
import {UserService} from '../../../../shared/services/user-service';
import {Thread} from '../../../../core/models/thread.model';
import {Button} from '../../../../shared/components/ui/button/button';
import {IconComponent} from '../../../../shared/components/ui/icon/icon';
import {RouterLink} from '@angular/router';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-compte-forum',
  imports: [
    Button,
    IconComponent,
    RouterLink,
    DatePipe
  ],
  templateUrl: './compte-forum.html',
  styleUrl: './compte-forum.scss'
})
export class CompteForum implements OnInit {

  private readonly threadService = inject(ThreadService);
  private readonly userProfile = inject(UserService);
  private readonly userId = this.userProfile.currentUserProfile?.user.id;
  threads = signal<Thread[]>([]);
  errorMessage: string | null = null;
  readonly isDeleting = signal<boolean>(false);

  ngOnInit(): void {
    this.loadThreads();
  }


  loadThreads() {
    this.threadService.getThreadsByUserId(this.userId).subscribe({
      next: (data) => {
        this.threads.set(data);
      },
      error: err => {
        this.errorMessage = err;
      }
    })
  }

  refreshThreads() {
    this.loadThreads();
  }

}
