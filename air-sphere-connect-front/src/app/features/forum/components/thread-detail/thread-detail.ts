import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ThreadService } from '../../../../core/services/thread.service';
import { PostService } from '../../../../core/services/post.service';
import { DatePipe } from '@angular/common';
import { PostComponent } from '../post/post';
import { switchMap, tap } from 'rxjs';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../../shared/services/user-service';
import { toSignal } from '@angular/core/rxjs-interop';
import { Post } from '../../../../core/models/post.model';

@Component({
  selector: 'app-thread-detail',
  standalone: true,
  imports: [DatePipe, RouterLink, PostComponent, ReactiveFormsModule],
  templateUrl: './thread-detail.html',
  styleUrls: ['./thread-detail.scss']
})
export class ThreadDetailComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private threadService = inject(ThreadService);
  private postService = inject(PostService);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  // Forms & State
  newPostForm = this.fb.group({
    content: ['', [Validators.required, Validators.minLength(2)]]
  });

  isPublishing = signal(false);
  isLoadingPosts = signal(false);
  errorMessage = signal<string | null>(null);

  postsSignal = signal<Post[]>([]);
  posts = computed(() => this.postsSignal());

  private routeParams = toSignal(this.route.paramMap);

  // Route params
  sectionId = computed(() => {
    const id = Number(this.routeParams()?.get('sectionId'));
    return isNaN(id) ? null : id;
  });

  threadId = computed(() => {
    const id = Number(this.routeParams()?.get('threadId'));
    return isNaN(id) ? null : id;
  });

  // Thread data
  thread = toSignal(
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = Number(params.get('threadId'));
        if (isNaN(id)) {
          this.router.navigate(['/forum']);
          throw new Error('Invalid thread ID');
        }
        return this.threadService.getThreadById(id);
      })
    )
  );

  constructor() {
    this.loadPosts();
  }

  private loadPosts(): void {
    this.isLoadingPosts.set(true);

    this.route.paramMap
      .pipe(
        takeUntilDestroyed(),
        tap(() => this.isLoadingPosts.set(true)),
        switchMap(params => {
          const id = Number(params.get('threadId'));
          if (isNaN(id)) {
            this.router.navigate(['/forum']);
            throw new Error('Invalid thread ID');
          }
          return this.postService.getPostByThreadId(id);
        })
      )
      .subscribe({
        next: posts => {
          this.postsSignal.set(posts);
          this.isLoadingPosts.set(false);
        },
        error: err => {
          console.error('Erreur chargement posts:', err);
          this.isLoadingPosts.set(false);
          this.errorMessage.set('Erreur lors du chargement des posts');
        }
      });
  }

  private getUserId(): number | null {
    const userId = this.userService.currentUserProfile?.user.id;
    if (!userId) {
      this.errorMessage.set('Vous devez être connecté');
    }
    return userId ?? null;
  }

  private updatePost(updated: Post): void {
    this.postsSignal.update(posts =>
      posts.map(p => (p.id === updated.id ? updated : p))
    );
  }

  publishPost(): void {
    if (this.newPostForm.invalid || this.isPublishing()) return;

    const content = this.newPostForm.value.content!.trim();
    const threadId = this.threadId();
    const currentUser = this.userService.currentUserProfile?.user;
    const userId = this.userService.currentUserProfile?.user.id;

    if (!threadId) {
      this.errorMessage.set('Thread introuvable');
      return;
    }

    if (!currentUser) {
      this.errorMessage.set('Vous devez être connecté');
      return;
    }

    this.isPublishing.set(true);

    this.postService.addPost(threadId, currentUser.username, content, userId).subscribe({
      next: newPost => {
        this.postsSignal.update(posts => [...posts, newPost]);
        this.newPostForm.reset();
        this.isPublishing.set(false);
      },
      error: err => {
        console.error('Erreur publication:', err);
        this.isPublishing.set(false);
      }
    });
  }

  onPostLike(postId: number): void {
    const userId = this.getUserId();
    if (!userId) return;

    this.postService.toggleReaction(postId, userId, 'LIKE').subscribe({
      next: updated => this.updatePost(updated),
      error: err => {
        console.error('Erreur like:', err);
        this.errorMessage.set('Erreur lors du like');
      }
    });
  }

  onPostDislike(postId: number): void {
    const userId = this.getUserId();
    if (!userId) return;

    this.postService.toggleReaction(postId, userId, 'DISLIKE').subscribe({
      next: updated => this.updatePost(updated),
      error: err => {
        console.error('Erreur dislike:', err);
        this.errorMessage.set('Erreur lors du dislike');
      }
    });
  }

  onPostFlagged(postId: number): void {

    const userId = this.getUserId();
    if (!userId) return;

    this.postService.toggleFlag(postId).subscribe({
      next: updated => this.updatePost(updated),
      error: err => {
        console.error('Erreur flag:', err);
        this.errorMessage.set('Erreur lors du signalement');
      }
    });
  }

  onPostDeleted(postId: number): void {
    const userId = this.getUserId();
    if (!userId) return;

    this.postService.deletePost(postId, userId).subscribe({
      next: () => {
        this.postsSignal.update(posts => posts.filter(p => p.id !== postId));
      },
      error: err => {
        console.error('Erreur suppression:', err);
        this.errorMessage.set('Erreur lors de la suppression');
      }
    });
  }
}
