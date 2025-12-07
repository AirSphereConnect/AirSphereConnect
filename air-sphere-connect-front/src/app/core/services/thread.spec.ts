import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ThreadService } from './thread.service';
import { Thread } from '../models/thread.model';
import { UserService } from '../../shared/services/user-service';
import { ApiConfigService } from './api';

describe('ThreadService', () => {
  let service: ThreadService;
  let httpMock: HttpTestingController;
  let mockUserService: jasmine.SpyObj<UserService>;
  let mockApiConfig: jasmine.SpyObj<ApiConfigService>;

  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    mockUserService = jasmine.createSpyObj('UserService', ['getUsername']);
    mockApiConfig = jasmine.createSpyObj('ApiConfigService', [], { apiUrl: mockApiUrl });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ThreadService,
        { provide: UserService, useValue: mockUserService },
        { provide: ApiConfigService, useValue: mockApiConfig }
      ]
    });

    service = TestBed.inject(ThreadService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all threads', () => {
    const mockThreads: Thread[] = [
      { id: 1, title: 'Thread 1', username: 'User1', createdAt: new Date(), rubricId: 1 },
      { id: 2, title: 'Thread 2', username: 'User2', createdAt: new Date(), rubricId: 1 }
    ];

    service.getAllThreads().subscribe(threads => {
      expect(threads.length).toBe(2);
      expect(threads).toEqual(mockThreads);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/forum-threads`);
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBe(true);
    req.flush(mockThreads);
  });

  it('should get thread by id', () => {
    const mockThread: Thread = {
      id: 1,
      title: 'Thread 1',
      username: 'User1',
      createdAt: new Date(),
      rubricId: 1
    };

    service.getThreadById(1).subscribe(thread => {
      expect(thread).toEqual(mockThread);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/forum-threads/1`);
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBe(true);
    req.flush(mockThread);
  });

  it('should get threads by section id', () => {
    const mockThreads: Thread[] = [
      { id: 1, title: 'Thread 1', username: 'User1', createdAt: new Date(), rubricId: 1 },
      { id: 2, title: 'Thread 2', username: 'User2', createdAt: new Date(), rubricId: 1 },
      { id: 3, title: 'Thread 3', username: 'User3', createdAt: new Date(), rubricId: 2 }
    ];

    service.getThreadsBySectionId(1).subscribe(threads => {
      expect(threads.length).toBe(2);
      expect(threads.every(t => t.rubricId === 1)).toBe(true);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/forum-threads`);
    expect(req.request.method).toBe('GET');
    req.flush(mockThreads);
  });

  it('should add a new thread', () => {
    const mockThread: Thread = {
      id: 1,
      title: 'New Thread',
      username: 'TestUser',
      createdAt: new Date(),
      rubricId: 1
    };

    mockUserService.getUsername.and.returnValue('TestUser');

    service.addThread('New Thread', 1, 123).subscribe(thread => {
      expect(thread).toEqual(mockThread);
    });

    const req = httpMock.expectOne(`${mockApiUrl}/forum-threads/new/123`);
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBe(true);
    expect(req.request.body.title).toBe('New Thread');
    expect(req.request.body.rubricId).toBe(1);
    req.flush(mockThread);
  });
});
