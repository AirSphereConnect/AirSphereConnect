import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { RoleGuard } from './RoleGuard';
import { UserService } from '../../shared/services/user-service';

describe('RoleGuard', () => {
  let guard: RoleGuard;
  let userService: jasmine.SpyObj<UserService>;
  let router: jasmine.SpyObj<Router>;
  let route: ActivatedRouteSnapshot;

  beforeEach(() => {
    const userServiceSpy = jasmine.createSpyObj('UserService', [], {
      currentUserProfile: null
    });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        RoleGuard,
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(RoleGuard);
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    route = {
      data: {},
      params: {},
      queryParams: {},
      url: [],
      fragment: null,
      outlet: '',
      component: null,
      routeConfig: null,
      root: {} as ActivatedRouteSnapshot,
      parent: null,
      firstChild: null,
      children: [],
      pathFromRoot: [],
      paramMap: {} as any,
      queryParamMap: {} as any,
      title: undefined
    };
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  describe('canActivate', () => {
    it('should allow activation when user has expected role (USER)', () => {
      route.data = { roles: 'USER' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'testuser', role: 'USER' } }),
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeTrue();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should allow activation when user has expected role (ADMIN)', () => {
      route.data = { roles: 'ADMIN' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'admin', role: 'ADMIN' } }),
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeTrue();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should allow activation when role is at profile level', () => {
      route.data = { roles: 'USER' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ role: 'USER' }),
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeTrue();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should deny activation and redirect when user does not have expected role', () => {
      route.data = { roles: 'ADMIN' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'testuser', role: 'USER' } }),
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/home']);
    });

    it('should deny activation when profile is null', () => {
      route.data = { roles: 'USER' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => null,
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/home']);
    });

    it('should deny activation when user and profile have no role', () => {
      route.data = { roles: 'USER' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'testuser' } }),
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/home']);
    });

    it('should work with multiple expected roles (role matches)', () => {
      route.data = { roles: 'USER,ADMIN' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'testuser', role: 'USER' } }),
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeTrue();
    });

    it('should work with multiple expected roles (no match)', () => {
      route.data = { roles: 'ADMIN,MODERATOR' };
      Object.defineProperty(userService, 'currentUserProfile', {
        get: () => ({ user: { id: 1, username: 'testuser', role: 'USER' } }),
        configurable: true
      });

      const result = guard.canActivate(route);

      expect(result).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/home']);
    });
  });
});
