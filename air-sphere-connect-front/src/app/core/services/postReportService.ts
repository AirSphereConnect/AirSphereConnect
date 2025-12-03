import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ApiConfigService} from './api';
import {PostReport, PostReportRequest} from '../models/post-report.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PostReportService {
  private http = inject(HttpClient);
  private api = inject(ApiConfigService);
  private apiUrlPosts = `${this.api.apiUrl}/post-reports`;

  createReport(request: PostReportRequest, userId: number): Observable<PostReport> {
    return this.http.post<PostReport>(`${this.apiUrlPosts}/new/${userId}`, request,
      {withCredentials: true}
    );
  }

  getReports(): Observable<PostReport[]> {
    return this.http.get<PostReport[]>(this.apiUrlPosts,
      {withCredentials: true}
    );
  }

  getReportByPostId(postId: number): Observable<PostReport[]> {
    return this.http.get<PostReport[]>(`${this.apiUrlPosts}/post/${postId}`,
      {withCredentials: true}
    );
  }

  // récupérer les reports par status
  getReportByStatus(status: string = 'PENDING'): Observable<PostReport[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<PostReport[]>( this.api.apiUrl,
      { params, withCredentials: true }
    );
  }

  // supprimer un report
  deleteReport(reportId: number, userId: number): Observable<void> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.delete<void>(`${this.apiUrlPosts}/${reportId}`,
      { params, withCredentials: true }
    );
  }
}
