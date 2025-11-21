import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Thread} from '../models/thread.model';
import {PostService} from './post.service';
import {UserService} from '../../shared/services/user-service';
import {map, Observable} from 'rxjs';
import { ApiConfigService } from './api';

@Injectable({
  providedIn: 'root'
})
export class ThreadService {
  private http = inject(HttpClient);
  private api = inject(ApiConfigService);
  private userService = inject(UserService);
  private apiUrl = `${this.api.apiUrl}/forum-threads`;

  getAllThreads(): Observable<Thread[]> {
    return this.http.get<Thread[]>(`${this.apiUrl}`,
      {withCredentials: true});
  }

  getThreadById(id: number): Observable<Thread> {
    return this.http.get<Thread>(`${this.apiUrl}/${id}`,
      { withCredentials: true});
  }

  getThreadsBySectionId(id: number): Observable<Thread[]> {
    return this.getAllThreads().pipe(
      map(threads => threads.filter((thread) => thread.rubricId === id)
        )
    );
  }

  addThread(title: string, sectionId: number, userId: number): Observable<Thread> {
    const newThread = {
      title: title,
      author: this.userService.getUsername(),
      createdAt: new Date(),
      rubricId: sectionId,
    };

    return this.http.post<Thread>(`${this.apiUrl}/new/${userId}`, newThread,
      { withCredentials: true}
    );
  }

  deleteThread(threadId: number, userId: number): Observable<void> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.delete<void>(`${this.apiUrl}/${threadId}`,
      { params, withCredentials: true }
    );
  }
}
