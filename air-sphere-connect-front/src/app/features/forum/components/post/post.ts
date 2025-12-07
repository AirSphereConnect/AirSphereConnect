import {Component, computed, inject, input, output, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {Post} from '../../../../core/models/post.model';
import {UserService} from '../../../../shared/services/user-service';
import {Button} from '../../../../shared/components/ui/button/button';
import {IconComponent} from '../../../../shared/components/ui/icon/icon';
import {PostReportReason} from '../../../../core/models/post-report.model';
import {PostReportService} from '../../../../core/services/postReportService';

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


  private readonly userService = inject(UserService);
  private readonly postReportService = inject(PostReportService)

  post = input.required<Post>()

  like = output<number>();
  dislike = output<number>();
  flag = output<{ postId: number; reason: PostReportReason; description: string }>();
  delete = output<number>();

  showFlagModal = signal(false);
  showDeleteModal = signal(false);
  flagConfirmed = signal(false);

  isLiked = computed(() => this.post().currentUserReaction === 'LIKE');
  isDisliked = computed(() => this.post().currentUserReaction === 'DISLIKE');

  // Etat du signalement
  selectedReason = signal<PostReportReason>(PostReportReason.SPAM)
  reportDescription = signal('');
  isReporting = signal(false);
  hasReported = signal(false);
  isCheckingReport = signal(true);

  // labels des raisons de signalement
  readonly reportReasons = [
    {value: PostReportReason.SPAM, label: 'Spam'},
    {value: PostReportReason.INAPPROPRIATE_CONTENT, label: 'Contenu inapproprié'},
    {value: PostReportReason.HARASSMENT, label: 'Harcèlement ou intimidation'},
    {value: PostReportReason.FALSE_INFORMATION, label: 'Fausse information'},
    {value: PostReportReason.OFF_TOPIC, label: 'Hors sujet'},
    {value: PostReportReason.COPYRIGHT_VIOLATION, label: 'Violation de droits d\'auteur'},
    {value: PostReportReason.OTHER, label: 'Autre raison'}
  ];

  canDelete = computed(() => {
    const currentUser = this.userService.currentUserProfile;
    const post = this.post();
    if (!currentUser) return false;

    if (currentUser.user.role === 'ADMIN') return true;

    return currentUser.user.id === post.userId;
  });

  isAuthenticated = computed(() => {
    return !!this.userService.currentUserProfile;
  });

// Vérifie si l'AUTEUR DU POST est le propriétaire du thread
  isAuthorThreadOwner = computed(() => {
    const post = this.post();
    return post.threadOwnerId !== undefined && post.userId === post.threadOwnerId;
  });

  onLikePost(postId: number): void {
    this.like.emit(postId);
  }

  onDislikePost(postId: number): void {
    this.dislike.emit(postId);
  }

// Modale de signalement
  openFlagModal(): void {
    // Si le post est déjà signalé, ne pas ouvrir la modale
    if (this.hasReported()) {
      alert('Vous avez déjà signalé ce post.')
      return;
    }
    this.showFlagModal.set(true);
  }

  closeFlagModal(): void {
    this.showFlagModal.set(false);
    this.selectedReason.set(PostReportReason.SPAM);
    this.reportDescription.set('');
  }

  confirmFlag(): void {
    const userId = this.userService.currentUserProfile?.user.id;
    if (!userId) {
      alert('vous devez être connecté pour signaler un post.')
      return;
    }

    this.isReporting.set(true);
    const request = {
      postId: this.post().id,
      reason: this.selectedReason(),
      description: this.reportDescription().trim() || 'Aucune description'
    };

    this.postReportService.createReport(request, userId).subscribe({
      next: () => {
        console.log('Post signalé avec succès');
        this.hasReported.set(true);
        this.closeFlagModal();
        this.isReporting.set(false);
        alert('Merci, votre signalement a été envoyé.');
      },
      error: (error) => {
        console.error('Erreur signalement', error);

        if (error.status === 401) {
          alert('Vous ne pouvez pas signaler votre propre post');
        } else if (error.error?.includes('déjà signalé')) {
          alert('Vous avez déjà signalé ce post');
          this.hasReported.set(true);
        } else {
          alert('Erreur lors du signalement');
        }

        this.isReporting.set(false);
      }
    });

  }

  // Modale de suppression
  openDeleteModal(): void {
    this.showDeleteModal.set(true);
  }

  closeDeleteModal(): void {
    this.showDeleteModal.set(false);
  }

  confirmDelete(): void {
    this.delete.emit(this.post().id);
    this.closeDeleteModal();
  }
}
