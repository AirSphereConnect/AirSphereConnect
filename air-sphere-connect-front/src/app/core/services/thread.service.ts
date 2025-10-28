import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Thread } from '../models/thread.model';
import { PostService } from './post.service';
import { UserService } from '../../shared/services/user-service';
import { Observable, switchMap } from 'rxjs';
import {Post} from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class ThreadService {
  private apiUrl = "http://localhost:8080/api/forum-threads";
  private http = inject(HttpClient);
  private postService = inject(PostService);
  private userService = inject(UserService);

  getAllThreads(): Observable<Thread[]> {
    return this.http.get<Thread[]>(`${this.apiUrl}`);
  }

  getThreadById(id: number): Observable<Thread> {
    return this.http.get<Thread>(`${this.apiUrl}/${id}`);
  }

  addThread(title: string, content: string, sectionId: number): Observable<Post> {
    const newThread = {
      title,
      author: this.userService.getUsername(),
      createdAt: new Date(),
      sectionId
    };

    return this.http.post<Thread>(`${this.apiUrl}`, newThread).pipe(
      switchMap(thread => this.postService.addPost(thread.id, thread.author, content))
    );
  }
}
