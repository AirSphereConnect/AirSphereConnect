import {Injectable, signal} from '@angular/core';
import {Post} from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private posts = signal<Post[]>([
    {
      id: 1,
      user: 'Cyril',
      content: 'Premier PostLorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.',
      createdAt: new Date(),
      likes: 2,
      isLiked: false,
      threadId: 1,
    },
    {
      id: 2,
      user: 'Bob',
      content: 'deuxieme Post',
      createdAt: new Date(),
      likes: 1,
      isLiked: true,
      threadId: 2
    }, {
      id: 3,
      user: 'Sandrine',
      content: 'troisieme Post',
      createdAt: new Date(),
      likes: 0,
      isLiked: false,
      threadId: 1
    },
    {
      id: 4,
      user: 'Nuno',
      content: 'quatrieme Post',
      createdAt: new Date(),
      likes: 0,
      isLiked: false,
      threadId: 2
    },
    {
      id: 5,
      user: 'Pierre',
      content: 'Ultime post',
      createdAt: new Date(),
      likes: 3,
      isLiked: false,
      threadId: 3
    }, {
      id: 6,
      user: 'Cyril',
      content: '5eme !',
      createdAt: new Date(),
      likes: 3,
      isLiked: true,
      threadId: 4
    },
    {
      id: 7,
      user: 'Bob',
      content: '6eme !',
      createdAt: new Date(),
      likes: 1,
      isLiked: false,
      threadId: 5
    }, {
      id: 8,
      user: 'Sandrine',
      content: 'Ouai !',
      createdAt: new Date(),
      likes: 0,
      isLiked: false,
      threadId: 6
    },
    {
      id: 9,
      user: 'Nuno',
      content: 'Post igjzipgf',
      createdAt: new Date(),
      likes: 1,
      isLiked: true,
      threadId: 6
    },
    {
      id: 10,
      user: 'Pierre',
      content: 'Ultime post 2',
      createdAt: new Date(),
      likes: 20,
      isLiked: true,
      threadId: 4
    },
  ]);

  // get all posts
  getPosts(): Post[] {
    return this.posts();
  }

  // get total likes by thread
  getPostLikesByThreadId(threadId: number): number {
    return this.getPostByThreadId(threadId).reduce((sum, post) => sum + post.likes, 0);
  }

  // retrieve all the posts of a thread
  getPostByThreadId(threadId: number): Post[] {
    return this.posts().filter(post => post.threadId === threadId);
  }

  // count the number of posts of a thread
  getPostCountByThreadId(threadId: number): number {
    return this.getPostByThreadId(threadId).length;
  }

  // add a post
  addPost(threadId: number, user: string, content: string) {
    const newPost: Post = {
      id: this.posts.length + 1,
      threadId,
      user,
      content,
      likes: 0,
      isLiked: false,
      createdAt: new Date(),
    };
    this.posts.update(posts => [...posts, newPost]);
  }

  // press the button Like !
  toggleLike(postId: number): Post | undefined {

    let updatedPost: Post | undefined;

    this.posts.update(posts =>
      posts.map(p => {
        if (p.id === postId) {
          const newPost = {
            ...p,
            isLiked: !p.isLiked,
            likes: p.isLiked ? Math.max(0, p.likes - 1) : p.likes + 1,
          };
          updatedPost = newPost;
          return newPost;
        }
        return p;
      })
    );
    return updatedPost;
  }
}
