import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {ThreadService} from '../../../../core/services/thread.service';
import {Thread} from '../../../../core/models/thread.model';

@Component({
  selector: 'app-thread-list',
  imports: [],
  templateUrl: './thread-list.html',
  styleUrls: ['./thread-list.scss']
})
export class ThreadListComponent {

  private router = inject(Router);
  private threadService = inject(ThreadService);

  threads: Thread[] = this.threadService.getAllThreads();

  openThread(threadId: number) {
    this.router.navigate(['/forum/thread-detail', threadId]).then(r => {});
  }

}
