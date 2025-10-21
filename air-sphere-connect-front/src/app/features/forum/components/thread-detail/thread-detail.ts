import {Component, inject, OnInit} from '@angular/core';
import {PostService} from '../../../../core/services/post.service';
import {Post} from '../../../../core/models/post.model';
import {PostComponent} from '../post/post';
import {ActivatedRoute, Router} from '@angular/router';
import {ThreadService} from '../../../../core/services/thread.service';
import {Thread} from '../../../../core/models/thread.model';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-thread-detail',
  imports: [
    DatePipe
  ],
  templateUrl: './thread-detail.html',
  styleUrls: ['./thread-detail.scss']
})
export class ThreadDetailComponent {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private threadService = inject(ThreadService);
  private postService = inject(PostService);

  thread?: Thread;
  posts: Post[] = [];

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.thread = this.threadService.getThreadById(id);
    this.posts = this.postService.getPostByThreadId(id);
  }

  goBack(): void {
    this.router.navigate(['/forum/thread-list']);
  }



}
