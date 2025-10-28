import {Component, inject, input, output, signal} from '@angular/core';
import {DatePipe} from '@angular/common';
import {Post} from '../../../../core/models/post.model';
import {PostService} from '../../../../core/services/post.service';
import {Button} from '../../../../shared/components/ui/button/button';
import {IconComponent} from '../../../../shared/components/ui/icon/icon';
import {Observable} from 'rxjs';

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

  post = input.required<Post>()

  onLike = output<number>();
  onFlag = output<number>();

  showFlagModal = signal(false);
  flagConfirmed = signal(false);

  onLikePost(): void {
    this.onLike.emit(this.post().id);
  }

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
}
