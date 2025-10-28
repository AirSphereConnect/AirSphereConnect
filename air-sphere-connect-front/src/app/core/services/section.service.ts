// section.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Section } from '../models/section.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SectionService {
  private apiUrl = "http://localhost:8080/api/forum-rubrics";
  private http = inject(HttpClient);

  getSections(): Observable<Section[]> {
    return this.http.get<Section[]>(this.apiUrl);
  }

  getSectionById(id: number): Observable<Section> {
    return this.http.get<Section>(`${this.apiUrl}/${id}`);
  }

  getThreadBySectionId(sectionId: number): Observable<Section> {
    return this.http.get<Section>(`${this.apiUrl}/${sectionId}/threads`);
  }
}
