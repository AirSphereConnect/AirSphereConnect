import {inject, Injectable} from '@angular/core';
import {Post} from '../models/post.model';
import {map, Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ApiConfigService} from './api';

type ReactionType = 'LIKE' | 'DISLIKE';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiConfigService);
  private readonly apiUrlPosts = `${this.api.apiUrl}/forum-posts`;


  getPosts(currentUserId?: number): Observable<Post[]> {
    let params = new HttpParams();
    if (currentUserId) {
      params = params.set('currentUserId', currentUserId.toString());
    }

    return this.http.get<Post[]>(`${this.apiUrlPosts}`,
      {
        params,
        withCredentials: true
      });
  }

  // retrieve all the posts of a thread
  getPostByThreadId(threadId: number, currentUserId?: number): Observable<Post[]> {
    return this.getPosts(currentUserId).pipe(
      map(posts => posts.filter(post => post.threadId === threadId)));
  }

  getPostCountByThreadId(threadId: number, currentUserId?: number): Observable<number> {
    return this.getPostByThreadId(threadId, currentUserId).pipe(
      map(posts => posts.length)
    );
  }

  getLikesByThreadId(threadId: number, currentUserId?: number): Observable<number> {
    return this.getPostByThreadId(threadId, currentUserId).pipe(
      map(posts => posts.reduce((sum, post) => sum + post.likeCount, 0))
    );
  }

  // add a post
  addPost(threadId: number, author: string, content: string, userId: number | undefined): Observable<Post> {
    const newPost = {
      threadId,
      author,
      content,
      createdAt: new Date(),
      likes: 0,
      isLiked: false,
      isFlagged: false
    };
    return this.http.post<Post>(`${this.apiUrlPosts}/new/${userId}`, newPost, {withCredentials: true});
  }

  deletePost(postId: number, userId: number): Observable<Post> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.delete<Post>(`${this.apiUrlPosts}/${postId}`, {params, withCredentials: true});
  }

  // press the button Like !
  toggleReaction(postId: number, userId: number | undefined, reactionType: ReactionType): Observable<Post> {
    if (userId === undefined) {
      throw new Error('User ID is undefined');
    }

    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('reaction', reactionType);

    return this.http.post<Post>(
      `${this.apiUrlPosts}/${postId}/reaction`,
      {},
      {params, withCredentials: true }
    );
  }
}
