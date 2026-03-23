import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SectionService } from './section.service';
import { ApiConfigService } from './api';
import { Section } from '../models/section.model';

describe('SectionService', () => {
  let service: SectionService;
  let httpMock: HttpTestingController;
  let apiConfig: jasmine.SpyObj<ApiConfigService>;
  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
      apiUrl: mockApiUrl
    });

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        SectionService,
        { provide: ApiConfigService, useValue: apiConfigSpy }
      ]
    });

    service = TestBed.inject(SectionService);
    httpMock = TestBed.inject(HttpTestingController);
    apiConfig = TestBed.inject(ApiConfigService) as jasmine.SpyObj<ApiConfigService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getSections', () => {
    it('should get all sections', () => {
      const mockSections: Section[] = [
        { id: 1, title: 'Section 1', description: 'Desc 1', user: 'user1' },
        { id: 2, title: 'Section 2', description: 'Desc 2', user: 'user2' }
      ];

      service.getSections().subscribe(sections => {
        expect(sections).toEqual(mockSections);
        expect(sections.length).toBe(2);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-rubrics`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockSections);
    });
  });

  describe('getSectionById', () => {
    it('should get section by id', () => {
      const sectionId = 1;
      const mockSection: Section = { id: 1, title: 'Section 1', description: 'Desc 1', user: 'user1' };

      service.getSectionById(sectionId).subscribe(section => {
        expect(section).toEqual(mockSection);
        expect(section.id).toBe(sectionId);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-rubrics/${sectionId}`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockSection);
    });
  });

  describe('createSection', () => {
    it('should create a new section', () => {
      const title = 'New Section';
      const description = 'New Description';
      const forumId = 1;
      const userId = 123;
      const mockResponse: Section = { id: 3, title, description, user: 'testuser' };

      service.createSection(title, description, forumId, userId).subscribe(section => {
        expect(section).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-rubrics/new/${userId}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ title, description, forumId });
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockResponse);
    });
  });

  describe('deleteSection', () => {
    it('should delete a section', () => {
      const sectionId = 1;
      const userId = 123;

      service.deleteSection(sectionId, userId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/forum-rubrics/${sectionId}` &&
        request.params.get('userId') === userId.toString()
      );
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });
});
