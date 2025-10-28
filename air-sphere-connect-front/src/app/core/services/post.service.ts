import {inject, Injectable} from '@angular/core';
import {Post} from '../models/post.model';
import {map, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrlPosts = "http://localhost:8080/api/forum-posts";
  private http = inject(HttpClient);


  getPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrlPosts}`);
  }

  // retrieve all the posts of a thread
  getPostByThreadId(threadId: number): Observable<Post[]> {
    return this.getPosts().pipe(
      map(posts => posts.filter(post => post.threadId === threadId))
    );
  }

  getPostCountByThreadId(threadId: number): Observable<number> {
    return this.getPostByThreadId(threadId).pipe(
      map(posts => posts.length)
    );
  }

  getLikesByThreadId(threadId: number): Observable<number> {
    return this.getPostByThreadId(threadId).pipe(
      map(posts => posts.reduce((sum, post) => sum + post.likes, 0))
    );
  }

  // add a post
  addPost(threadId: number, author: string, content: string): Observable<Post> {
    const newPost = {
      threadId,
      author,
      content,
      createdAt: new Date(),
      likes: 0,
      isLiked: false,
      isFlagged: false
    };
    return this.http.post<Post>(this.apiUrlPosts, newPost);
  }

  updatePost(post: Post): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrlPosts}/${post.id}`, post);
  }

  getPostStats(threadId: number) {
    return this.getPostByThreadId(threadId).pipe(
      map(posts => ({
        totalPosts: posts.length,
        totalLikes: posts.reduce((sum, post) => sum + post.likes, 0)
      }))
    );
  }

  // press the button Like !
  toggleLike(postId: number): Observable<Post> {
    return this.getPosts().pipe(
      map(posts => {
        const post = posts.find(p => p.id === postId);
        if (!post) throw new Error('Post not found');
        return {
          ...post,
          isLiked: !post.isLiked,
          likes: post.isLiked ? Math.max(0, post.likes - 1) : post.likes + 1,
        };
      })
    );
  }

  toggleFlag(postId: number): Observable<Post> {

    return this.getPosts().pipe(
      map(posts => {
        const post = posts.find(p => p.id === postId);
        if (!post) throw new Error('Post not found');
        return {
          ...post,
          isLiked: !post.isFlagged,
          likes: post.isLiked ? Math.max(0, post.likes - 1) : post.likes + 1,
        };
      })
    );
  }
}

