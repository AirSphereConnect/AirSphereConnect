import {Component, Input} from '@angular/core';
import {DatePipe} from '@angular/common';
import { Post } from '../../../../core/models/post.model';

@Component({
  selector: 'app-post',
  imports: [
    DatePipe
  ],
  templateUrl: './post.html',
  styleUrls: ['./post.scss']
})
export class PostComponent {

  @Input() post!: Post;
}
