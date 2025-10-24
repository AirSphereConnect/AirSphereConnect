import {Component, inject, input, Input, output} from '@angular/core';
import {DatePipe} from '@angular/common';
import {Post} from '../../../../core/models/post.model';
import {PostService} from '../../../../core/services/post.service';

@Component({
  selector: 'app-post',
  standalone: true,
  imports: [
    DatePipe
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
