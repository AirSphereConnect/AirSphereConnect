// section.service.ts
import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Section} from '../models/section.model';
import {Observable} from 'rxjs';
import {ApiConfigService} from './api';

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

  createSection(title: string, description: string, forumId: number, userId: number): Observable<Section> {
    const newSection = {title, description, forumId};
    return this.http.post<Section>(`${this.apiUrl}/new/${userId}`, newSection,
      {withCredentials: true}
    );
  }

  deleteSection(sectionId: number, userId: number): Observable<void> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.delete<void>(`${this.apiUrl}/${sectionId}`,
      {params, withCredentials: true }
    );
  }
}
