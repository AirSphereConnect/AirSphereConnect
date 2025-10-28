import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ThreadService } from '../../../../core/services/thread.service';
import { PostService } from '../../../../core/services/post.service';
import { Thread } from '../../../../core/models/thread.model';
import { Post } from '../../../../core/models/post.model';
import { DatePipe, AsyncPipe } from '@angular/common';
import { PostComponent } from '../post/post';
import { BehaviorSubject, Observable, switchMap, tap } from 'rxjs';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-thread-detail',
  standalone: true,
  imports: [
    DatePipe,
    RouterLink,
    PostComponent,
    ReactiveFormsModule,
    AsyncPipe
  ],
  templateUrl: './thread-detail.html',
  styleUrls: ['./thread-detail.scss']
})
export class ThreadDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private threadService = inject(ThreadService);
  private postService = inject(PostService);

  thread$!: Observable<Thread>;
  posts$!: Observable<Post[]>;
  sectionId: number | null = null;

  newPostContent = new FormControl('');
  isPublishing = new BehaviorSubject<boolean>(false);

  private refreshPosts$ = new BehaviorSubject<void>(undefined);

  ngOnInit(): void {
    const sectionId = this.route.snapshot.paramMap.get('sectionId');
    const threadId = Number(this.route.snapshot.paramMap.get('threadId'));

    if (!sectionId || isNaN(Number(sectionId)) || isNaN(threadId)) {
      this.router.navigate(['/forum']);
      return;
    }

    this.sectionId = Number(sectionId);
    this.thread$ = this.threadService.getThreadById(threadId);

    this.posts$ = this.refreshPosts$.pipe(
      switchMap(() => this.postService.getPostByThreadId(threadId))
    );
  }

  onPostLike(postId: number): void {
    this.postService.toggleLike(postId).pipe(
      tap(() => this.refreshPosts$.next())
    ).subscribe({
      error: (error) => console.error('Erreur lors du like:', error)
    });
  }

  onPostFlagged(post: Post): void {
    this.postService.toggleFlag(post.id).pipe(
      tap(() => this.refreshPosts$.next())
    ).subscribe({
      error: (error) => console.error('Erreur lors du signalement:', error)
    });
  }

  publishPost(): void {
    const content = this.newPostContent.value?.trim();

    if (!content) {
      console.log('Le contenu ne peut pas être vide');
      return;
    }

    const threadId = Number(this.route.snapshot.paramMap.get('threadId'));
    if (!threadId) {
      console.log('Thread introuvable');
      return;
    }

    this.isPublishing.next(true);

    this.postService.addPost(threadId, 'Utilisateur actuel', content).pipe(
      tap(() => {
        this.newPostContent.reset();
        this.refreshPosts$.next();
        this.isPublishing.next(false);
      })
    ).subscribe({
      error: (error) => {
        console.error('Erreur lors de la publication:', error);
        this.isPublishing.next(false);
      }
    });
  }
}
