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
    { id: 1, title: 'Thread bonjour eco', author: 'admin', createdAt: new Date(), sectionId:1 },
    { id: 2, title: 'Thread vive l\' eco', author: 'Nuno', createdAt: new Date(), sectionId:1 },
    { id: 3, title: 'Thread vive Montpellier tout vert', author: 'Sandrine', createdAt: new Date(), sectionId: 1 },
    { id: 4, title: 'Thread projet', author: 'cyril',  createdAt: new Date(), sectionId: 2 },
    { id: 5, title: 'Thread Vive la pluie', author: 'Guest',  createdAt: new Date(), sectionId: 2 },
    { id: 6, title: 'Thread Vive le vert', author: 'Greta',  createdAt: new Date(), sectionId: 3 },
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
