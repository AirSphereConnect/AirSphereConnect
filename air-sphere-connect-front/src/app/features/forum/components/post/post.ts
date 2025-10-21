import {Component, Input} from '@angular/core';
import {Post} from '../../../../core/models/post.model';

@Component({
  selector: 'app-post',
  imports: [],
  templateUrl: './post.html',
  styleUrls: ['./post.scss']
})
export class PostComponent {

  @Input() post!: Post;
}
