// section.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Section } from '../models/section.model';
import { Observable } from 'rxjs';
import {Thread} from '../models/thread.model';
import { ApiConfigService } from './api';

@Injectable({
  providedIn: 'root'
})
export class SectionService {
  private http = inject(HttpClient);
  private api = inject(ApiConfigService);
  private apiUrl = `${this.api.apiUrl}/forum-rubrics`;

  getSections(): Observable<Section[]> {
    return this.http.get<Section[]>(this.apiUrl,
      {withCredentials: true}
    );
  }

  getSectionById(id: number): Observable<Section> {
    return this.http.get<Section>(`${this.apiUrl}/${id}`,
      { withCredentials: true }
      );
  }
}
