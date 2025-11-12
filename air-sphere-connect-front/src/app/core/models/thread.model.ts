import {Post} from './post.model';
import {Section} from './section.model';

export interface Thread {
  id: number;
  title: string;
  author: string;
  createdAt: Date;
  sectionId: number;
}
