import {Component, inject, input, Input, output} from '@angular/core';
import {DatePipe} from '@angular/common';
import {Post} from '../../../../core/models/post.model';
import {PostService} from '../../../../core/services/post.service';
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

  post = input.required<Post>()
  onLike = output<number>();

  onLikePost(): void {
    this.onLike.emit(this.post().id);
  }

}
