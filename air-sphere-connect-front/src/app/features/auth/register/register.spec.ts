import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';

import { Register } from './register';
import { UserService } from '../../../shared/services/user-service';
import { CityService } from '../../../core/services/city';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let userService: jasmine.SpyObj<UserService>;
  let cityService: jasmine.SpyObj<CityService>;
  let router: Router;

  const mockCity = {
    id: 1,
    name: 'Paris',
    inseeCode: '75056',
    postalCode: '75000',
    areaCode: '75',
    latitude: 48.8566,
    longitude: 2.3522,
    population: 2161000
  };

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['checkAvailability', 'register', 'setUserProfile']);
    const cityServiceSpy = jasmine.createSpyObj('CityService', ['searchCities']);

    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: UserService, useValue: userServiceSpy },
        { provide: CityService, useValue: cityServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    cityService = TestBed.inject(CityService) as jasmine.SpyObj<CityService>;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form initialization', () => {
    it('should initialize step to 1', () => {
      expect(component.step()).toBe(1);
    });

    it('should initialize registerFirstForm with validators', () => {
      expect(component.registerFirstForm).toBeDefined();
      const usernameControl = component.registerFirstForm.get('username');
      const emailControl = component.registerFirstForm.get('email');
      const passwordControl = component.registerFirstForm.get('password');

      expect(usernameControl).toBeDefined();
      expect(emailControl).toBeDefined();
      expect(passwordControl).toBeDefined();
    });

    it('should initialize registerForm with validators', () => {
      expect(component.registerForm).toBeDefined();
      const addressControl = component.registerForm.get('address');
      const cityNameControl = component.registerForm.get('cityName');
      const cityCodeControl = component.registerForm.get('cityCode');

      expect(addressControl).toBeDefined();
      expect(cityNameControl).toBeDefined();
      expect(cityCodeControl).toBeDefined();
    });

    it('should provide form control getters', () => {
      expect(component.usernameControl as any).toEqual(component.registerFirstForm.get('username'));
      expect(component.emailControl as any).toEqual(component.registerFirstForm.get('email'));
      expect(component.passwordControl as any).toEqual(component.registerFirstForm.get('password'));
      expect(component.addressControl as any).toEqual(component.registerForm.get('address'));
      expect(component.cityNameControl as any).toEqual(component.registerForm.get('cityName'));
      expect(component.cityCodeControl as any).toEqual(component.registerForm.get('cityCode'));
    });
  });

  describe('Signals', () => {
    it('should initialize signals with default values', () => {
      expect(component.errorMessage()).toBeNull();
      expect(component.isLoadingStep1()).toBeFalse();
      expect(component.isLoadingStep2()).toBeFalse();
      expect(component.passwordVisible()).toBeFalse();
      expect(component.canSubmitStep1()).toBeFalse();
      expect(component.canSubmitStep2()).toBeFalse();
    });

    it('should initialize cityQuery to empty string', () => {
      expect(component.cityQuery()).toBe('');
    });

    it('should initialize citySuggestions to empty array', () => {
      expect(component.citySuggestions()).toEqual([]);
    });

    it('should initialize cityIdSelected to null', () => {
      expect(component.cityIdSelected).toBeNull();
    });
  });

  describe('Username validation', () => {
    it('should be invalid when username is empty', () => {
      component.usernameControl.setValue('');
      expect(component.usernameControl.hasError('required')).toBeTrue();
    });

    it('should be invalid when username is too short', () => {
      component.usernameControl.setValue('ab');
      expect(component.usernameControl.hasError('minlength')).toBeTrue();
    });

    it('should be invalid when username is too long', () => {
      component.usernameControl.setValue('a'.repeat(26));
      expect(component.usernameControl.hasError('maxlength')).toBeTrue();
    });

    it('should be invalid when username contains invalid characters', () => {
      component.usernameControl.setValue('user@name');
      expect(component.usernameControl.hasError('invalidUsername')).toBeTrue();
    });

    it('should be valid with alphanumeric and underscore/dash', () => {
      component.usernameControl.setValue('user_name-123');
      expect(component.usernameControl.valid).toBeTrue();
    });
  });

  describe('Email validation', () => {
    it('should be invalid when email is empty', () => {
      component.emailControl.setValue('');
      expect(component.emailControl.hasError('required')).toBeTrue();
    });

    it('should be invalid with simple string', () => {
      component.emailControl.setValue('notanemail');
      expect(component.emailControl.hasError('invalidEmail')).toBeTrue();
    });

    it('should be invalid without domain', () => {
      component.emailControl.setValue('user@');
      expect(component.emailControl.hasError('invalidEmail')).toBeTrue();
    });

    it('should be invalid without TLD', () => {
      component.emailControl.setValue('user@domain');
      expect(component.emailControl.hasError('invalidEmail')).toBeTrue();
    });

    it('should be invalid with short domain parts', () => {
      component.emailControl.setValue('user@d.c');
      expect(component.emailControl.hasError('invalidEmail')).toBeTrue();
    });

    it('should be valid with proper email format', () => {
      component.emailControl.setValue('user@example.com');
      expect(component.emailControl.valid).toBeTrue();
    });

    it('should be valid with complex email', () => {
      component.emailControl.setValue('user.name+tag@sub.domain.com');
      expect(component.emailControl.valid).toBeTrue();
    });
  });

  describe('Password validation', () => {
    it('should be invalid when password is empty', () => {
      component.passwordControl.setValue('');
      expect(component.passwordControl.hasError('required')).toBeTrue();
    });

    it('should be invalid when password is too short', () => {
      component.passwordControl.setValue('Pass1@');
      expect(component.passwordControl.hasError('minlength')).toBeTrue();
    });

    it('should be invalid when password is too long', () => {
      component.passwordControl.setValue('A'.repeat(26) + 'a1@');
      expect(component.passwordControl.hasError('maxlength')).toBeTrue();
    });

    it('should be invalid without uppercase', () => {
      component.passwordControl.setValue('password1@');
      expect(component.passwordControl.hasError('noUpperCase')).toBeTrue();
    });

    it('should be invalid without lowercase', () => {
      component.passwordControl.setValue('PASSWORD1@');
      expect(component.passwordControl.hasError('noLowerCase')).toBeTrue();
    });

    it('should be invalid without number', () => {
      component.passwordControl.setValue('Password@');
      expect(component.passwordControl.hasError('noNumber')).toBeTrue();
    });

    it('should be invalid without special character', () => {
      component.passwordControl.setValue('Password1');
      expect(component.passwordControl.hasError('noSpecialChar')).toBeTrue();
    });

    it('should be valid with all requirements', () => {
      component.passwordControl.setValue('Password1@');
      expect(component.passwordControl.valid).toBeTrue();
    });
  });

  describe('Computed signals', () => {
    it('should compute passwordIcon as eye when password is hidden', () => {
      component.passwordVisible.set(false);
      expect(component.passwordIcon()).toBe('eye');
    });

    it('should compute passwordIcon as eyeSlash when password is visible', () => {
      component.passwordVisible.set(true);
      expect(component.passwordIcon()).toBe('eyeSlash');
    });

    it('should compute passwordType as password when hidden', () => {
      component.passwordVisible.set(false);
      expect(component.passwordType()).toBe('password');
    });

    it('should compute passwordType as text when visible', () => {
      component.passwordVisible.set(true);
      expect(component.passwordType()).toBe('text');
    });
  });

  describe('togglePasswordVisibility', () => {
    it('should toggle passwordVisible from false to true', () => {
      component.passwordVisible.set(false);
      component.togglePasswordVisibility();
      expect(component.passwordVisible()).toBeTrue();
    });

    it('should toggle passwordVisible from true to false', () => {
      component.passwordVisible.set(true);
      component.togglePasswordVisibility();
      expect(component.passwordVisible()).toBeFalse();
    });
  });

  describe('City search and selection', () => {
    it('should update cityQuery on input', () => {
      const event = { target: { value: 'Paris' } } as any;
      component.onCityInput(event);
      expect(component.cityQuery()).toBe('Paris');
    });

    it('should select city and update form', () => {
      component.selectCity(mockCity);

      expect(component.cityNameControl.value).toBe('Paris');
      expect(component.cityCodeControl.value).toBe('75000');
      expect(component.cityIdSelected).toBe(1);
      expect(component.citySuggestions()).toEqual([]);
    });
  });

  describe('clearError', () => {
    it('should clear error message', () => {
      component.errorMessage.set('Some error');
      component.clearError();
      expect(component.errorMessage()).toBeNull();
    });
  });

  describe('onFirstSubmit - Step 1', () => {
    beforeEach(() => {
      component.registerFirstForm.patchValue({
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1@'
      });
    });

    it('should not submit if form is invalid', () => {
      component.registerFirstForm.patchValue({ username: '' });
      component.onFirstSubmit();
      expect(userService.checkAvailability).not.toHaveBeenCalled();
    });

    it('should not submit if already loading', () => {
      component.isLoadingStep1.set(true);
      component.onFirstSubmit();
      expect(userService.checkAvailability).not.toHaveBeenCalled();
    });

    it('should set isLoadingStep1 to true', () => {
      const checkSubject = new Subject<any>();
      userService.checkAvailability.and.returnValue(checkSubject.asObservable());
      component.onFirstSubmit();
      expect(component.isLoadingStep1()).toBeTrue();
      checkSubject.next({ usernameTaken: false, emailTaken: false });
      checkSubject.complete();
    });

    it('should clear errorMessage before checking', () => {
      component.errorMessage.set('Previous error');
      userService.checkAvailability.and.returnValue(of({ usernameTaken: false, emailTaken: false }));
      component.onFirstSubmit();
      expect(component.errorMessage()).toBeNull();
    });

    it('should call checkAvailability with username and email', () => {
      userService.checkAvailability.and.returnValue(of({ usernameTaken: false, emailTaken: false }));
      component.onFirstSubmit();
      expect(userService.checkAvailability).toHaveBeenCalledWith('testuser', 'test@example.com');
    });

    describe('when username and email are available', () => {
      it('should move to step 2', (done) => {
        userService.checkAvailability.and.returnValue(of({ usernameTaken: false, emailTaken: false }));
        component.onFirstSubmit();

        setTimeout(() => {
          expect(component.step()).toBe(2);
          expect(component.errorMessage()).toBeNull();
          expect(component.isLoadingStep1()).toBeFalse();
          done();
        });
      });
    });

    describe('when username is taken', () => {
      it('should show error and stay on step 1', (done) => {
        userService.checkAvailability.and.returnValue(of({ usernameTaken: true, emailTaken: false }));
        component.onFirstSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe("Nom d'utilisateur déjà pris.");
          expect(component.step()).toBe(1);
          expect(component.isLoadingStep1()).toBeFalse();
          done();
        });
      });
    });

    describe('when email is taken', () => {
      it('should show error and stay on step 1', (done) => {
        userService.checkAvailability.and.returnValue(of({ usernameTaken: false, emailTaken: true }));
        component.onFirstSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Adresse email déjà utilisée.');
          expect(component.step()).toBe(1);
          expect(component.isLoadingStep1()).toBeFalse();
          done();
        });
      });
    });

    describe('error handling', () => {
      it('should handle network error (status 0)', (done) => {
        userService.checkAvailability.and.returnValue(throwError(() => ({ status: 0 })));
        component.onFirstSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Impossible de contacter le serveur.');
          expect(component.isLoadingStep1()).toBeFalse();
          done();
        });
      });

      it('should handle 404 error', (done) => {
        userService.checkAvailability.and.returnValue(throwError(() => ({ status: 404 })));
        component.onFirstSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Service non disponible.');
          done();
        });
      });

      it('should handle 500 error', (done) => {
        userService.checkAvailability.and.returnValue(throwError(() => ({ status: 500 })));
        component.onFirstSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Erreur serveur (500).');
          done();
        });
      });

      it('should handle other errors', (done) => {
        userService.checkAvailability.and.returnValue(throwError(() => ({ status: 418 })));
        component.onFirstSubmit();

        setTimeout(() => {
          expect(component.errorMessage()).toBe('Erreur serveur (418).');
          done();
        });
      });
    });
  });

  describe('onSubmit - Step 2', () => {
    beforeEach(() => {
      component.registerFirstForm.patchValue({
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1@'
      });
      component.registerForm.patchValue({
        address: '123 Rue de la Paix',
        cityName: 'Paris',
        cityCode: '75000'
      });
      component.cityIdSelected = 1;
    });

    it('should not submit if registerForm is invalid', () => {
      component.registerForm.patchValue({ address: '' });
      component.onSubmit();
      expect(userService.register).not.toHaveBeenCalled();
    });

    it('should not submit if registerFirstForm is invalid', () => {
      component.registerFirstForm.patchValue({ username: '' });
      component.onSubmit();
      expect(userService.register).not.toHaveBeenCalled();
    });

    it('should not submit if already loading', () => {
      component.isLoadingStep2.set(true);
      component.onSubmit();
      expect(userService.register).not.toHaveBeenCalled();
    });

    it('should set isLoadingStep2 to true', () => {
      const registerSubject = new Subject<any>();
      userService.register.and.returnValue(registerSubject.asObservable());
      component.onSubmit();
      expect(component.isLoadingStep2()).toBeTrue();
      registerSubject.next({
        user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER', address: null as any, favorites: [], alerts: [] },
        role: 'USER'
      } as any);
      registerSubject.complete();
    });

    it('should clear errorMessage before registering', () => {
      component.errorMessage.set('Previous error');
      userService.register.and.returnValue(of({
        user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER', address: null as any, favorites: [], alerts: [] },
        role: 'USER'
      } as any));
      component.onSubmit();
      expect(component.errorMessage()).toBeNull();
    });

    it('should call register with correct payload', () => {
      userService.register.and.returnValue(of({
        user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER', address: null as any, favorites: [], alerts: [] },
        role: 'USER'
      } as any));
      component.onSubmit();

      expect(userService.register).toHaveBeenCalledWith({
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1@',
        address: {
          street: '123 Rue de la Paix',
          city: { id: 1 }
        }
      });
    });

    it('should set user profile after success', (done) => {
      const mockResponse = {
        user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER', address: null as any, favorites: [], alerts: [] },
        role: 'USER'
      };
      userService.register.and.returnValue(of(mockResponse as any));
      component.onSubmit();

      setTimeout(() => {
        expect(userService.setUserProfile).toHaveBeenCalledWith(mockResponse as any);
        done();
      });
    });

    it('should navigate to /home after success', (done) => {
      userService.register.and.returnValue(of({
        user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER', address: null as any, favorites: [], alerts: [] },
        role: 'USER'
      } as any));
      component.onSubmit();

      setTimeout(() => {
        expect(router.navigate).toHaveBeenCalledWith(['/home']);
        done();
      });
    });

    it('should set isLoadingStep2 to false after success', (done) => {
      userService.register.and.returnValue(of({
        user: { id: 1, username: 'testuser', email: 'test@example.com', role: 'USER', address: null as any, favorites: [], alerts: [] },
        role: 'USER'
      } as any));
      component.onSubmit();

      setTimeout(() => {
        expect(component.isLoadingStep2()).toBeFalse();
        done();
      });
    });

    it('should handle registration error', (done) => {
      userService.register.and.returnValue(throwError(() => new Error('Registration failed')));
      component.onSubmit();

      setTimeout(() => {
        expect(component.errorMessage()).toBe("Erreur lors de l'inscription.");
        expect(component.isLoadingStep2()).toBeFalse();
        expect(router.navigate).not.toHaveBeenCalled();
        done();
      });
    });
  });

  describe('goBackToStep1', () => {
    it('should go back to step 1', () => {
      component.step.set(2);
      component.goBackToStep1();
      expect(component.step()).toBe(1);
    });

    it('should clear errorMessage', () => {
      component.errorMessage.set('Some error');
      component.goBackToStep1();
      expect(component.errorMessage()).toBeNull();
    });
  });

  describe('Form status changes', () => {
    it('should update canSubmitStep1 when registerFirstForm changes', (done) => {
      component.registerFirstForm.patchValue({
        username: 'validuser',
        email: 'valid@example.com',
        password: 'Password1@'
      });

      setTimeout(() => {
        expect(component.canSubmitStep1()).toBeTrue();
        done();
      }, 100);
    });

    it('should update canSubmitStep2 when registerForm changes', (done) => {
      component.registerForm.patchValue({
        address: '123 Rue de la Paix',
        cityName: 'Paris',
        cityCode: '75000'
      });

      setTimeout(() => {
        expect(component.canSubmitStep2()).toBeTrue();
        done();
      }, 100);
    });
  });
});
