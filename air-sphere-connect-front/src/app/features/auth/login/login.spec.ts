import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';

import { Login } from './login';
import { UserService } from '../../../shared/services/user-service';
import { UserProfileResponse } from '../../../core/models/user.model';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let userService: jasmine.SpyObj<UserService>;
  let router: Router;

  const mockUserProfile: UserProfileResponse = {
    user: { id: 1, username: 'testuser', role: 'USER' },
    role: 'USER'
  } as UserProfileResponse;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['login']);

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: UserService, useValue: userServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form initialization', () => {
    it('should initialize loginForm with empty fields', () => {
      expect(component.loginForm).toBeDefined();
      expect(component.loginForm.get('username')?.value).toBe('');
      expect(component.loginForm.get('password')?.value).toBe('');
    });

    it('should have required validators on username and password', () => {
      const usernameControl = component.loginForm.get('username');
      const passwordControl = component.loginForm.get('password');

      usernameControl?.setValue('');
      passwordControl?.setValue('');

      expect(usernameControl?.hasError('required')).toBeTrue();
      expect(passwordControl?.hasError('required')).toBeTrue();
    });

    it('should provide usernameControl getter', () => {
      expect(component.usernameControl as any).toEqual(component.loginForm.get('username'));
    });

    it('should provide passwordControl getter', () => {
      expect(component.passwordControl as any).toEqual(component.loginForm.get('password'));
    });
  });

  describe('Signals', () => {
    it('should initialize errorMessage to null', () => {
      expect(component.errorMessage()).toBeNull();
    });

    it('should initialize isLoading to false', () => {
      expect(component.isLoading()).toBeFalse();
    });

    it('should initialize passwordType to password', () => {
      expect(component.passwordType()).toBe('password');
    });
  });

  describe('Computed signals', () => {
    it('should compute isFormValid as false when form is invalid', () => {
      component.loginForm.patchValue({ username: '', password: '' });
      expect(component.isFormValid()).toBeFalse();
    });

    it('should compute isFormValid as true when form is valid', () => {
      component.loginForm.patchValue({ username: 'testuser', password: 'password123' });
      expect(component.isFormValid()).toBeTrue();
    });

    it('should compute canSubmit as false when form is invalid', () => {
      component.loginForm.patchValue({ username: '', password: '' });
      expect(component.canSubmit()).toBeFalse();
    });

    it('should compute canSubmit as false when loading', () => {
      component.loginForm.patchValue({ username: 'testuser', password: 'password123' });
      component.isLoading.set(true);
      expect(component.canSubmit()).toBeFalse();
    });

    it('should compute canSubmit as true when form is valid and not loading', () => {
      component.loginForm.patchValue({ username: 'testuser', password: 'password123' });
      component.isLoading.set(false);
      expect(component.canSubmit()).toBeTrue();
    });

    it('should compute passwordIcon as eye when passwordType is password', () => {
      component.passwordType.set('password');
      expect(component.passwordIcon()).toBe('eye');
    });

    it('should compute passwordIcon as eyeSlash when passwordType is text', () => {
      component.passwordType.set('text');
      expect(component.passwordIcon()).toBe('eyeSlash');
    });
  });

  describe('togglePasswordVisibility', () => {
    it('should toggle passwordType from password to text', () => {
      component.passwordType.set('password');
      component.togglePasswordVisibility();
      expect(component.passwordType()).toBe('text');
    });

    it('should toggle passwordType from text to password', () => {
      component.passwordType.set('text');
      component.togglePasswordVisibility();
      expect(component.passwordType()).toBe('password');
    });
  });

  describe('clearError', () => {
    it('should clear errorMessage', () => {
      component.errorMessage.set('Some error');
      component.clearError();
      expect(component.errorMessage()).toBeNull();
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      component.loginForm.patchValue({
        username: 'testuser',
        password: 'password123'
      });
    });

    it('should not submit if form is invalid', () => {
      component.loginForm.patchValue({ username: '', password: '' });
      component.onSubmit();
      expect(userService.login).not.toHaveBeenCalled();
    });

    it('should not submit if already loading', () => {
      component.isLoading.set(true);
      component.onSubmit();
      expect(userService.login).not.toHaveBeenCalled();
    });

    it('should set isLoading to true during login', () => {
      const loginSubject = new Subject<UserProfileResponse>();
      userService.login.and.returnValue(loginSubject.asObservable());
      component.onSubmit();
      expect(component.isLoading()).toBeTrue();
      loginSubject.next(mockUserProfile);
      loginSubject.complete();
    });

    it('should clear errorMessage before login', () => {
      component.errorMessage.set('Previous error');
      userService.login.and.returnValue(of(mockUserProfile));
      component.onSubmit();
      expect(component.errorMessage()).toBeNull();
    });

    it('should call userService.login with credentials', () => {
      userService.login.and.returnValue(of(mockUserProfile));
      component.onSubmit();
      expect(userService.login).toHaveBeenCalledWith({
        username: 'testuser',
        password: 'password123'
      });
    });

    describe('Successful login', () => {
      it('should set isLoading to false after success', (done) => {
        userService.login.and.returnValue(of(mockUserProfile));
        component.onSubmit();

        setTimeout(() => {
          expect(component.isLoading()).toBeFalse();
          done();
        });
      });

      it('should clear errorMessage after success', (done) => {
        userService.login.and.returnValue(of(mockUserProfile));
        component.errorMessage.set('Previous error');
        component.onSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBeNull();
          done();
        });
      });

      it('should navigate to /home after success', (done) => {
        userService.login.and.returnValue(of(mockUserProfile));
        component.onSubmit();

        setTimeout(() => {
          expect(router.navigate).toHaveBeenCalledWith(['/home']);
          done();
        });
      });
    });

    describe('Login errors', () => {
      it('should set isLoading to false after error', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 401 })));
        component.onSubmit();

        setTimeout(() => {
          expect(component.isLoading()).toBeFalse();
          done();
        });
      });

      it('should handle status 0 (network error)', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 0 })));
        component.onSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Impossible de contacter le serveur.');
          done();
        });
      });

      it('should handle status 401 (unauthorized)', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 401 })));
        component.onSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe("Nom d'utilisateur ou mot de passe incorrect.");
          done();
        });
      });

      it('should handle status 403 (forbidden)', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 403 })));
        component.onSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Accès refusé.');
          done();
        });
      });

      it('should handle status 404 (not found)', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 404 })));
        component.onSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Service non disponible.');
          done();
        });
      });

      it('should handle status 500 (server error)', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 500 })));
        component.onSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Erreur serveur. Veuillez réessayer plus tard.');
          done();
        });
      });

      it('should handle unknown error status', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 418 })));
        component.onSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Une erreur est survenue lors de la connexion.');
          done();
        });
      });

      it('should not navigate on error', (done) => {
        userService.login.and.returnValue(throwError(() => ({ status: 401 })));
        component.onSubmit();

        setTimeout(() => {
          expect(router.navigate).not.toHaveBeenCalled();
          done();
        });
      });
    });
  });
});
