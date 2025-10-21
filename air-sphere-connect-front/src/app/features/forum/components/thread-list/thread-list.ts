import {Component, inject} from '@angular/core';
import {ForumService} from '../../services/forum.service';
import {Router} from '@angular/router';
import {ThreadService} from '../../services/thread.service';
import {Thread} from '../../../../core/models/thread.model';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-thread-list',
  imports: [],
  templateUrl: './thread-list.html',
  styleUrls: ['./thread-list.scss']
})
export class ThreadList {

  private threads = inject(ThreadService)

  constructor(private threadService: ThreadService, private router: Router) {
  }

}
