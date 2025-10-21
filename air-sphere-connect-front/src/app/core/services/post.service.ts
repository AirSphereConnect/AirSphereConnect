import {Injectable} from '@angular/core';
import {Post} from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private posts: Post[] = [
    {
      id: 1,
      user: 'Cyril',
      content: 'Premier Post',
      createdAt: new Date(),
      threadId: 1
    },
    {
      id: 2,
      user: 'Bob',
      content: 'deuxieme Post',
      createdAt: new Date(),
      threadId: 2
    },{
      id: 3,
      user: 'Sandrine',
      content: 'troisieme Post',
      createdAt: new Date(),
      threadId: 1
    },
    {
      id: 4,
      user: 'Nuno',
      content: 'quatrieme Post',
      createdAt: new Date(),
      threadId: 2
    },
    {
      id: 5,
      user: 'Pierre',
      content: 'Ultime post',
      createdAt: new Date(),
      threadId: 3
    },
  ];

  getPosts(): Post[] {
    return this.posts;
  }

  getPostByThreadId(threadId: number) {
    return this.posts.filter(post => post.threadId === threadId);
  }

  addPost(threadId: number, user: string, content: string) {
    const newPost: Post = {
      id: this.posts.length + 1,
      threadId,
      user,
      content,
      createdAt: new Date(),
    };
    this.posts.push(newPost);
  }


}
