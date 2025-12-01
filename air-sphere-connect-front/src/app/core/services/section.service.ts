// section.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Section } from '../models/section.model';
import { Observable } from 'rxjs';
import { ApiConfigService } from './api';

@Injectable({
  providedIn: 'root'
})
export class SectionService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiConfigService);
  private readonly apiUrl = `${this.api.apiUrl}/forum-rubrics`;

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
