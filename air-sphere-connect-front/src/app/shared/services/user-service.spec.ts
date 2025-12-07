import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user-service';
import { ApiConfigService } from '../../core/services/api';
import { UserProfileResponse, RegisterPayload, UpdateUserPayload, UpdateAddressPayload } from '../../core/models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  let apiConfig: jasmine.SpyObj<ApiConfigService>;
  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    localStorage.clear();

    const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
      apiUrl: mockApiUrl
    });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        UserService,
        { provide: ApiConfigService, useValue: apiConfigSpy }
      ]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
    apiConfig = TestBed.inject(ApiConfigService) as jasmine.SpyObj<ApiConfigService>;
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Constructor', () => {
    it('should load user profile from localStorage on init', () => {
      TestBed.resetTestingModule();
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test', role: 'USER' } } as any;
      localStorage.setItem('userProfile', JSON.stringify(mockProfile));

      const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
        apiUrl: mockApiUrl
      });

      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          UserService,
          { provide: ApiConfigService, useValue: apiConfigSpy }
        ]
      });

      const newService = TestBed.inject(UserService);
      newService.userProfile$.subscribe(profile => {
        expect(profile).toEqual(mockProfile);
      });
    });

    it('should handle missing localStorage data', () => {
      TestBed.resetTestingModule();
      localStorage.removeItem('userProfile');

      const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
        apiUrl: mockApiUrl
      });

      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [
          UserService,
          { provide: ApiConfigService, useValue: apiConfigSpy }
        ]
      });

      const newService = TestBed.inject(UserService);
      newService.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });
    });
  });

  describe('currentUserProfile', () => {
    it('should return current user profile', () => {
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;
      service.setUserProfile(mockProfile);

      expect(service.currentUserProfile).toEqual(mockProfile);
    });

    it('should return null when no profile', () => {
      service.setUserProfile(null);
      expect(service.currentUserProfile).toBeNull();
    });
  });

  describe('fetchUserProfile', () => {
    it('should fetch and set user profile when user exists', () => {
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;

      service.fetchUserProfile();

      const req = httpMock.expectOne(`${mockApiUrl}/profile`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockProfile);

      service.userProfile$.subscribe(profile => {
        expect(profile).toEqual(mockProfile);
      });
    });

    it('should set null when profile.user is null (guest)', () => {
      service.fetchUserProfile();

      const req = httpMock.expectOne(`${mockApiUrl}/profile`);
      req.flush({ user: null } as unknown as UserProfileResponse);

      service.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });
    });

    it('should set null on 401 error', () => {
      service.fetchUserProfile();

      const req = httpMock.expectOne(`${mockApiUrl}/profile`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      service.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });
    });

    it('should set null on other errors', () => {
      service.fetchUserProfile();

      const req = httpMock.expectOne(`${mockApiUrl}/profile`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });

      service.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });
    });
  });

  describe('setUserProfile', () => {
    it('should save profile to localStorage and emit', (done) => {
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;

      service.setUserProfile(mockProfile);

      const stored = localStorage.getItem('userProfile');
      expect(stored).toBe(JSON.stringify(mockProfile));

      service.userProfile$.subscribe(profile => {
        expect(profile).toEqual(mockProfile);
        done();
      });
    });

    it('should remove profile from localStorage when null', () => {
      localStorage.setItem('userProfile', 'some data');
      service.setUserProfile(null);

      expect(localStorage.getItem('userProfile')).toBeNull();
    });
  });

  describe('login', () => {
    it('should login and set user profile', (done) => {
      const credentials = { username: 'test', password: 'pass' };
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;

      service.login(credentials).subscribe(profile => {
        expect(profile).toEqual(mockProfile);
        done();
      });

      const loginReq = httpMock.expectOne(`${mockApiUrl}/login`);
      expect(loginReq.request.method).toBe('POST');
      expect(loginReq.request.body).toEqual(credentials);
      expect(loginReq.request.withCredentials).toBeTrue();
      loginReq.flush(mockProfile);

      const profileReq = httpMock.expectOne(`${mockApiUrl}/profile`);
      profileReq.flush(mockProfile);
    });

    it('should set null on login error', (done) => {
      const credentials = { username: 'test', password: 'wrong' };

      service.login(credentials).subscribe({
        error: () => {
          service.userProfile$.subscribe(profile => {
            expect(profile).toBeNull();
            done();
          });
        }
      });

      const req = httpMock.expectOne(`${mockApiUrl}/login`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('logout', () => {
    it('should logout and clear user profile', fakeAsync(() => {
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;
      service.setUserProfile(mockProfile);

      service.logout().subscribe();

      const req = httpMock.expectOne(`${mockApiUrl}/logout`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);

      service.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });

      tick(600);
      const profileReq = httpMock.expectOne(`${mockApiUrl}/profile`);
      profileReq.flush({ user: null } as unknown as UserProfileResponse);
    }));

    it('should handle logout error gracefully', () => {
      service.logout().subscribe();

      const req = httpMock.expectOne(`${mockApiUrl}/logout`);
      req.flush('Error', { status: 500, statusText: 'Server Error' });

      service.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });
    });
  });

  describe('checkAvailability', () => {
    it('should check username and email availability', () => {
      const username = 'testuser';
      const email = 'test@example.com';
      const mockResponse = { usernameTaken: false, emailTaken: true };

      service.checkAvailability(username, email).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/users/check` &&
        request.params.get('username') === username &&
        request.params.get('email') === email
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockResponse);
    });
  });

  describe('register', () => {
    it('should register a new user', () => {
      const payload: RegisterPayload = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'Pfhsidg',
        address: { street: '123 Street', city: { id: 1 } }
      };
      const mockResponse: UserProfileResponse = { user: { id: 1, username: 'newuser' } } as any;

      service.register(payload).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/users/signup`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(payload);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockResponse);
    });
  });

  describe('editUser', () => {
    it('should edit user and update profile', (done) => {
      const userId = 1;
      const payload: UpdateUserPayload = { username: 'updated' };
      const mockResponse: UserProfileResponse = { user: { id: 1, username: 'updated' } } as any;

      service.editUser(userId, payload).subscribe(response => {
        expect(response).toEqual(mockResponse);
        done();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/users/${userId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(payload);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockResponse);

      service.userProfile$.subscribe(profile => {
        expect(profile).toEqual(mockResponse);
      });
    });
  });

  describe('deleteUser', () => {
    it('should delete user and fetch profile', () => {
      const userId = 1;

      service.deleteUser(userId).subscribe();

      const deleteReq = httpMock.expectOne(`${mockApiUrl}/users?id=${userId}`);
      expect(deleteReq.request.method).toBe('DELETE');
      expect(deleteReq.request.withCredentials).toBeTrue();
      deleteReq.flush(null);

      const profileReq = httpMock.expectOne(`${mockApiUrl}/profile`);
      profileReq.flush({ user: null } as unknown as UserProfileResponse);
    });
  });

  describe('editAddress', () => {
    it('should edit address', () => {
      const userId = 1;
      const payload: UpdateAddressPayload = { street: 'New Street', city: { id: 2 } };

      service.editAddress(userId, payload).subscribe();

      const req = httpMock.expectOne(`${mockApiUrl}/address/${userId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(payload);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });

  describe('getUsername', () => {
    it('should return username when profile exists', () => {
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'testuser' } } as any;
      service.setUserProfile(mockProfile);

      expect(service.getUsername()).toBe('testuser');
    });

    it('should return null when profile is null', () => {
      service.setUserProfile(null);
      expect(service.getUsername()).toBeNull();
    });
  });
});
