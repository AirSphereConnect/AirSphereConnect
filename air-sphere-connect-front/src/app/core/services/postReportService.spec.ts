import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostReportService } from './postReportService';
import { ApiConfigService } from './api';
import { PostReport, PostReportRequest, PostReportReason } from '../models/post-report.model';

describe('PostReportService', () => {
  let service: PostReportService;
  let httpMock: HttpTestingController;
  let apiConfig: jasmine.SpyObj<ApiConfigService>;
  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
      apiUrl: mockApiUrl
    });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PostReportService,
        { provide: ApiConfigService, useValue: apiConfigSpy }
      ]
    });

    service = TestBed.inject(PostReportService);
    httpMock = TestBed.inject(HttpTestingController);
    apiConfig = TestBed.inject(ApiConfigService) as jasmine.SpyObj<ApiConfigService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('createReport', () => {
    it('should create a new report', () => {
      const request: PostReportRequest = { postId: 1, reason: PostReportReason.SPAM, description: 'Test spam report' };
      const userId = 123;
      const mockResponse: PostReport = {
        id: 1,
        postId: 1,
        reason: PostReportReason.SPAM,
        description: 'Test spam report',
        status: 'PENDING',
        userId: userId,
        createdAt: '2024-01-01T12:00:00',
        updatedAt: '2024-01-01T12:00:00',
        deletedAt: ''
      };

      service.createReport(request, userId).subscribe(report => {
        expect(report).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/post-reports/new/${userId}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockResponse);
    });
  });

  describe('getReports', () => {
    it('should get all reports', () => {
      const mockReports: PostReport[] = [
        {
          id: 1,
          postId: 1,
          reason: PostReportReason.SPAM,
          description: 'Spam report',
          status: 'PENDING',
          userId: 1,
          createdAt: '2024-01-01T12:00:00',
          updatedAt: '2024-01-01T12:00:00',
          deletedAt: ''
        },
        {
          id: 2,
          postId: 2,
          reason: PostReportReason.INAPPROPRIATE_CONTENT,
          description: 'Offensive content',
          status: 'RESOLVED',
          userId: 2,
          createdAt: '2024-01-01T12:00:00',
          updatedAt: '2024-01-01T12:00:00',
          deletedAt: ''
        }
      ];

      service.getReports().subscribe(reports => {
        expect(reports).toEqual(mockReports);
        expect(reports.length).toBe(2);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/post-reports`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockReports);
    });
  });

  describe('getReportByPostId', () => {
    it('should get reports for a specific post', () => {
      const postId = 5;
      const mockReports: PostReport[] = [
        {
          id: 1,
          postId: 5,
          reason: PostReportReason.SPAM,
          description: 'Spam report',
          status: 'PENDING',
          userId: 1,
          createdAt: '2024-01-01T12:00:00',
          updatedAt: '2024-01-01T12:00:00',
          deletedAt: ''
        }
      ];

      service.getReportByPostId(postId).subscribe(reports => {
        expect(reports).toEqual(mockReports);
        expect(reports[0].postId).toBe(postId);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/post-reports/post/${postId}`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockReports);
    });
  });

  describe('getReportByStatus', () => {
    it('should get reports with default status PENDING', () => {
      const mockReports: PostReport[] = [
        {
          id: 1,
          postId: 1,
          reason: PostReportReason.SPAM,
          description: 'Spam report',
          status: 'PENDING',
          userId: 1,
          createdAt: '2024-01-01T12:00:00',
          updatedAt: '2024-01-01T12:00:00',
          deletedAt: ''
        }
      ];

      service.getReportByStatus().subscribe(reports => {
        expect(reports).toEqual(mockReports);
      });

      const req = httpMock.expectOne(request =>
        request.url === mockApiUrl && request.params.get('status') === 'PENDING'
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockReports);
    });

    it('should get reports with custom status', () => {
      const mockReports: PostReport[] = [
        {
          id: 2,
          postId: 2,
          reason: PostReportReason.INAPPROPRIATE_CONTENT,
          description: 'Offensive content',
          status: 'RESOLVED',
          userId: 2,
          createdAt: '2024-01-01T12:00:00',
          updatedAt: '2024-01-01T12:00:00',
          deletedAt: ''
        }
      ];

      service.getReportByStatus('RESOLVED').subscribe(reports => {
        expect(reports).toEqual(mockReports);
      });

      const req = httpMock.expectOne(request =>
        request.url === mockApiUrl && request.params.get('status') === 'RESOLVED'
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockReports);
    });
  });

  describe('deleteReport', () => {
    it('should delete a report', () => {
      const reportId = 1;
      const userId = 123;

      service.deleteReport(reportId, userId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/post-reports/${reportId}` &&
        request.params.get('userId') === userId.toString()
      );
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });
});
