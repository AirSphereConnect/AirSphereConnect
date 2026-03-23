import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ThreadService } from './thread.service';
import { ApiConfigService } from './api';
import { UserService } from '../../shared/services/user-service';
import { Thread } from '../models/thread.model';

describe('ThreadService', () => {
  let service: ThreadService;
  let httpMock: HttpTestingController;
  let apiConfig: jasmine.SpyObj<ApiConfigService>;
  let userService: jasmine.SpyObj<UserService>;
  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
      apiUrl: mockApiUrl
    });
    const userServiceSpy = jasmine.createSpyObj('UserService', ['getUsername']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ThreadService,
        { provide: ApiConfigService, useValue: apiConfigSpy },
        { provide: UserService, useValue: userServiceSpy }
      ]
    });

    service = TestBed.inject(ThreadService);
    httpMock = TestBed.inject(HttpTestingController);
    apiConfig = TestBed.inject(ApiConfigService) as jasmine.SpyObj<ApiConfigService>;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllThreads', () => {
    it('should get all threads', () => {
      const mockThreads: Thread[] = [
        { id: 1, title: 'Thread 1', username: 'user1', createdAt: new Date('2024-01-01T12:00:00'), rubricId: 1 },
        { id: 2, title: 'Thread 2', username: 'user2', createdAt: new Date('2024-01-01T13:00:00'), rubricId: 1 }
      ];

      service.getAllThreads().subscribe(threads => {
        expect(threads).toEqual(mockThreads);
        expect(threads.length).toBe(2);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-threads`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockThreads);
    });
  });

  describe('getThreadById', () => {
    it('should get thread by id', () => {
      const threadId = 1;
      const mockThread: Thread = { id: 1, title: 'Thread 1', username: 'user1', createdAt: new Date('2024-01-01T12:00:00'), rubricId: 1 };

      service.getThreadById(threadId).subscribe(thread => {
        expect(thread).toEqual(mockThread);
        expect(thread.id).toBe(threadId);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-threads/${threadId}`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockThread);
    });
  });

  describe('getThreadsBySectionId', () => {
    it('should filter threads by section id', () => {
      const sectionId = 2;
      const mockThreads: Thread[] = [
        { id: 1, title: 'Thread 1', username: 'user1', createdAt: new Date('2024-01-01T12:00:00'), rubricId: 1 },
        { id: 2, title: 'Thread 2', username: 'user2', createdAt: new Date('2024-01-01T13:00:00'), rubricId: 2 },
        { id: 3, title: 'Thread 3', username: 'user3', createdAt: new Date('2024-01-01T14:00:00'), rubricId: 2 }
      ];

      service.getThreadsBySectionId(sectionId).subscribe(threads => {
        expect(threads.length).toBe(2);
        expect(threads.every(t => t.rubricId === sectionId)).toBeTrue();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-threads`);
      req.flush(mockThreads);
    });

    it('should return empty array when no threads match section id', () => {
      const sectionId = 999;
      const mockThreads: Thread[] = [
        { id: 1, title: 'Thread 1', username: 'user1', createdAt: new Date('2024-01-01T12:00:00'), rubricId: 1 }
      ];

      service.getThreadsBySectionId(sectionId).subscribe(threads => {
        expect(threads.length).toBe(0);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-threads`);
      req.flush(mockThreads);
    });
  });

  describe('addThread', () => {
    it('should add a new thread', () => {
      const title = 'New Thread';
      const sectionId = 1;
      const userId = 123;
      const username = 'testuser';
      userService.getUsername.and.returnValue(username);

      const mockResponse: Thread = {
        id: 4,
        title,
        username: username,
        createdAt: new Date('2024-01-01T12:00:00'),
        rubricId: sectionId
      };

      service.addThread(title, sectionId, userId).subscribe(thread => {
        expect(thread.id).toBe(4);
        expect(thread.title).toBe(title);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/forum-threads/new/${userId}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.title).toBe(title);
      expect(req.request.body.author).toBe(username);
      expect(req.request.body.rubricId).toBe(sectionId);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockResponse);
    });
  });

  describe('deleteThread', () => {
    it('should delete a thread', () => {
      const threadId = 1;
      const userId = 123;

      service.deleteThread(threadId, userId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/forum-threads/${threadId}` &&
        request.params.get('userId') === userId.toString()
      );
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });
});
