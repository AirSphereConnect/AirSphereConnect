import {Component, computed, inject, input, output, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {Post} from '../../../../core/models/post.model';
import {PostService} from '../../../../core/services/post.service';
import {Subject} from 'rxjs';
import {UserService} from '../../../../shared/services/user-service';
import {Button} from '../../../../shared/components/ui/button/button';
import {IconComponent} from '../../../../shared/components/ui/icon/icon';

@Component({
  selector: 'app-post',
  standalone: true,
  imports: [
    DatePipe,
    Button,
    IconComponent
  ],
  templateUrl: './post.html',
  styleUrls: ['./post.scss']
})
export class PostComponent {

  private postService = inject(PostService);
  private userService = inject(UserService);

  readonly refreshPosts$ = new Subject<void>()

  post = input.required<Post>()

  onLike = output<number>();
  onDislike = output<number>();
  onFlag = output<number>();
  onDelete = output<number>();

  showFlagModal = signal(false);
  showDeleteModal = signal(false);
  flagConfirmed = signal(false);

  isLiked = computed(() => this.post().currentUserReaction ==='LIKE');
  isDisliked = computed(() => this.post().currentUserReaction ==='DISLIKE');
  hasReacted = computed(() => this.post().currentUserReaction !== null);

  canDelete = computed(() => {
    const currentUser = this.userService.currentUserProfile;
    const post = this.post();
    if (!currentUser) return false;

    if(currentUser.user.role === 'ADMIN') return true;

    return currentUser.user.id === post.userId;
  });

  isAuthenticated = computed(() => {
    const currentUser = this.userService.currentUserProfile;
    return currentUser && currentUser.user.role === 'ADMIN';
  });

  onLikePost(postId: number): void {
    this.onLike.emit(postId);
  }

  onDislikePost(postId: number): void {
    this.onDislike.emit(postId);
  }
// Modale de signalement
  openFlagModal(): void {
    // Si le post est déjà signalé, ne pas ouvrir la modale
    if (this.post().isFlagged) {
      console.log('Ce post est déjà signalé !');
      return;
    }

    this.showFlagModal.set(true);
    this.flagConfirmed.set(false);
  }

  closeFlagModal(): void {
    this.showFlagModal.set(false);
    this.flagConfirmed.set(false);
  }

  confirmFlag(): void {
   this.onFlag.emit(this.post().id)
    this.flagConfirmed.set(true);
  }

  // Modale de suppression
  openDeleteModal(): void {
    this.showDeleteModal.set(true);
  }

  closeDeleteModal(): void {
    this.showDeleteModal.set(false);
  }

  confirmDelete(): void {
    this.onDelete.emit(this.post().id);
    this.closeDeleteModal();
  }
}
