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
      content: 'Premier PostLorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
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
    },{
      id: 6,
      user: 'Cyril',
      content: '5eme !',
      createdAt: new Date(),
      threadId: 4
    },
    {
      id: 7,
      user: 'Bob',
      content: '6eme !',
      createdAt: new Date(),
      threadId: 5
    },{
      id: 8,
      user: 'Sandrine',
      content: 'Ouai !',
      createdAt: new Date(),
      threadId: 6
    },
    {
      id: 9,
      user: 'Nuno',
      content: 'Post igjzipgf',
      createdAt: new Date(),
      threadId: 6
    },
    {
      id: 10,
      user: 'Pierre',
      content: 'Ultime post 2',
      createdAt: new Date(),
      threadId: 4
    },
  ];

  getPosts(): Post[] {
    return this.posts;
  }

  getPostByThreadId(threadId: number): Post[] {
    return this.posts.filter(post => post.threadId === threadId);
  }

  getPostCountByThreadId(threadId: number): number {
    return this.getPostByThreadId(threadId).length;
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
