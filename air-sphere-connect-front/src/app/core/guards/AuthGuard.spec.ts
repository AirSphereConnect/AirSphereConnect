import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './AuthGuard';
import { UserService } from '../../shared/services/user-service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let userService: jasmine.SpyObj<UserService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const userServiceSpy = jasmine.createSpyObj('UserService', [], {
      currentUserProfile: null
    });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(AuthGuard);
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate', () => {
    it('should allow activation when user is authenticated and not GUEST', () => {
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'testuser' }, role: 'USER' }),
        configurable: true
      });

      const result = guard.canActivate();

      expect(result).toBeTrue();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should allow activation when user role is ADMIN', () => {
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'admin' }, role: 'ADMIN' }),
        configurable: true
      });

      const result = guard.canActivate();

      expect(result).toBeTrue();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should deny activation and redirect when user is null', () => {
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => null,
        configurable: true
      });

      const result = guard.canActivate();

      expect(result).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should deny activation and redirect when user role is GUEST', () => {
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ role: 'GUEST' }),
        configurable: true
      });

      const result = guard.canActivate();

      expect(result).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should deny activation when user object exists but role is GUEST', () => {
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: null, role: 'GUEST' }),
        configurable: true
      });

      const result = guard.canActivate();

      expect(result).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
    });
  });
});
