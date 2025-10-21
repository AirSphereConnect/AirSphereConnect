import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Thread} from '../models/thread.model';
import {PostService} from './post.service';
import {Post} from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class ThreadService {
  //private apiUrl = "http://localhost:8080/api/forum-threads";
  //constructor(private http: HttpClient) { }
  private postService = inject(PostService);

  private threads: Thread[] = [
    { id: 1, title: 'bonjour eco', author: 'admin' },
    { id: 2, title: 'projet', author: 'cyril' },
    { id: 3, title: 'Vive le vert', author: 'Greta' },
  ]



  getAllThreads() {
  //  return this.http.get<Thread[]>(`${this.apiUrl}`);
    return this.threads;
  }


  getThreadById(id: number) {
   // return this.http.get<Thread>(`${this.apiUrl}/${id}`);
    return this.threads.find(thread => thread.id === id);
  }





}
