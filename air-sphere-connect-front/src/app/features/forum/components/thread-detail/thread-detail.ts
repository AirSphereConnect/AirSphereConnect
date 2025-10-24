import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {ThreadService} from '../../../../core/services/thread.service';
import {PostService} from '../../../../core/services/post.service';
import {Thread} from '../../../../core/models/thread.model';
import {Post} from '../../../../core/models/post.model';
import {DatePipe} from '@angular/common';
import {PostComponent} from '../post/post';

@Component({
  selector: 'app-thread-detail',
  standalone: true,
  imports: [DatePipe, RouterLink, PostComponent],
  templateUrl: './thread-detail.html',
  styleUrls: ['./thread-detail.scss']
})
export class ThreadDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private threadService = inject(ThreadService);
  private postService = inject(PostService);

  thread = signal<Thread | undefined>(undefined);
  posts = signal<Post[]>([]);
  sectionId = signal<string | null>(null);


  ngOnInit(): void {
    const sectionId = this.route.snapshot.paramMap.get('sectionId');
    const threadId = Number(this.route.snapshot.paramMap.get('threadId'));

    this.sectionId.set(sectionId);
    const sectionIdNumber = Number(sectionId)

    if (!sectionId || isNaN(sectionIdNumber) || isNaN(threadId) || sectionIdNumber <= 0 || threadId <= 0) {
      this.router.navigate(['/forum']);
      return;
    }

    const foundThread = this.threadService.getThreadById(threadId);
    if (foundThread && foundThread.sectionId === sectionIdNumber) {
      this.thread.set(foundThread);
      this.posts.set(this.postService.getPostByThreadId(threadId));
    } else {
      this.router.navigate(['/forum']);
    }
  }

  totalLikes = computed(() => {
    return this.posts().reduce((total, post) => total + post.likes, 0);
  })

  onPostLiked(postId: number): void {
    const updatedPost = this.postService.toggleLike(postId);

    if(updatedPost) {
      const threadId = this.thread()?.id;

      if (threadId) {
        this.posts.set(this.postService.getPostByThreadId(threadId));
      }
      if (updatedPost.isLiked) {
        console.log(`Post ${postId} a été aimé ! 👍 Total likes: ${updatedPost.likes}`);
      } else {
        console.log(`Post ${postId} n'est plus aimé. 👎 Total likes: ${updatedPost.likes}`);
      }
    }



  }
}
