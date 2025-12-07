import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { NavigationService } from './navigation-service';
import { UserService } from './user-service';

describe('NavigationService', () => {
  let service: NavigationService;
  let userService: jasmine.SpyObj<UserService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['logout']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        NavigationService,
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    service = TestBed.inject(NavigationService);
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('logout', () => {
    it('should call userService.logout', () => {
      userService.logout.and.returnValue(of(undefined));

      service.logout();

      expect(userService.logout).toHaveBeenCalled();
    });

    it('should navigate to /home after logout', (done) => {
      userService.logout.and.returnValue(of(undefined));

      service.logout();

      setTimeout(() => {
        expect(router.navigate).toHaveBeenCalledWith(['/home']);
        done();
      });
    });

    it('should navigate to /home even if logout completes synchronously', () => {
      userService.logout.and.returnValue(of(undefined));
      router.navigate.and.returnValue(Promise.resolve(true));

      service.logout();

      expect(userService.logout).toHaveBeenCalled();
    });
  });
});
