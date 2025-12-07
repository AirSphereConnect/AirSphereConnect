import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { of, throwError, BehaviorSubject, Subject } from 'rxjs';

import { EmailForm } from './email-form';
import { UserService } from '../../../services/user-service';
import { NotificationService } from '../../../services/notification-service';
import {User, UserProfileResponse} from '../../../../core/models/user.model';

describe('EmailForm', () => {
  let component: EmailForm;
  let fixture: ComponentFixture<EmailForm>;
  let userService: jasmine.SpyObj<UserService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let userProfileSubject: BehaviorSubject<any>;

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    role: 'USER',
    address: {
      id: 1,
      street: '123 Test St',
      city: { name: 'Paris', postalCode: '75001' }
    },
    favorites: [],
    alerts: []
  };

  const mockUserProfileResponse: UserProfileResponse = {
    user: mockUser,
    role: 'USER'
  };


  beforeEach(async () => {
    userProfileSubject = new BehaviorSubject<any>({ user: { id: 1, email: 'test@example.com' } });

    const userServiceSpy = jasmine.createSpyObj('UserService', [
      'editUser',
      'fetchUserProfile'
    ], {
      userProfile$: userProfileSubject.asObservable()
    });

    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    await TestBed.configureTestingModule({
      imports: [EmailForm],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: UserService, useValue: userServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmailForm);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form initialization', () => {
    it('should initialize form with empty email field', () => {
      expect(component.emailForm).toBeDefined();
      expect(component.emailForm.get('email')?.value).toBe('');
    });

    it('should have required validator on email field', () => {
      const emailControl = component.emailForm.get('email');
      emailControl?.setValue('');
      expect(emailControl?.hasError('required')).toBeTrue();
    });

    it('should have email validator on email field', () => {
      const emailControl = component.emailForm.get('email');
      emailControl?.setValue('invalid-email');
      expect(emailControl?.hasError('email')).toBeTrue();
    });

    it('should subscribe to userProfile$ and set user', () => {
      const newProfile = { user: { id: 2, email: 'newuser@example.com' } };
      userProfileSubject.next(newProfile);
      expect(component.user).toEqual(newProfile.user);
    });
  });

  describe('Form validation', () => {
    it('should be invalid when email is empty', () => {
      component.emailForm.get('email')?.setValue('');
      expect(component.emailForm.valid).toBeFalse();
    });

    it('should be invalid when email format is incorrect', () => {
      component.emailForm.get('email')?.setValue('not-an-email');
      expect(component.emailForm.valid).toBeFalse();
    });

    it('should be valid when email is correctly formatted', () => {
      component.emailForm.get('email')?.setValue('valid@example.com');
      expect(component.emailForm.valid).toBeTrue();
    });
  });

  describe('ngOnChanges', () => {
    it('should patch form with initialEmailData when provided', () => {
      component.initialEmailData = { email: 'initial@example.com' };
      component.ngOnChanges();
      expect(component.emailForm.get('email')?.value).toBe('initial@example.com');
    });

    it('should not patch form when initialEmailData is null', () => {
      component.emailForm.get('email')?.setValue('current@example.com');
      component.initialEmailData = null;
      component.ngOnChanges();
      expect(component.emailForm.get('email')?.value).toBe('current@example.com');
    });
  });

  describe('submit', () => {
    beforeEach(() => {
      component.editingUserId = 1;
      component.emailForm.get('email')?.setValue('newemail@example.com');
      component.emailForm.markAsDirty();
    });

    it('should show error message if form is invalid', () => {
      component.emailForm.get('email')?.setValue('');
      component.submit();
      expect(notificationService.showError).toHaveBeenCalledWith('Veuillez renseigner une adresse email.');
      expect(userService.editUser).not.toHaveBeenCalled();
    });

    it('should show error message if form is not dirty', () => {
      component.emailForm.markAsPristine();
      component.submit();
      expect(notificationService.showError).toHaveBeenCalledWith('Veuillez renseigner une adresse email.');
      expect(userService.editUser).not.toHaveBeenCalled();
    });

    it('should call editUser with correct parameters on valid submission', () => {
      userService.editUser.and.returnValue(of(mockUserProfileResponse));

      component.submit();

      expect(userService.editUser).toHaveBeenCalledWith(1, { email: 'newemail@example.com' });
    });

    it('should set isLoading to true during submission', () => {
      const editSubject = new Subject<UserProfileResponse>();
      userService.editUser.and.returnValue(editSubject.asObservable());

      component.submit();

      expect(component.isLoading()).toBeTrue();
      editSubject.next(mockUserProfileResponse);
      editSubject.complete();
    });

    it('should set isLoading to false after successful submission', (done) => {
      userService.editUser.and.returnValue(of(mockUserProfileResponse));

      component.submit();

      setTimeout(() => {
        expect(component.isLoading()).toBeFalse();
        done();
      });
    });

    it('should call fetchUserProfile after successful submission', (done) => {
      userService.editUser.and.returnValue(of(mockUserProfileResponse));

      component.submit();

      setTimeout(() => {
        expect(userService.fetchUserProfile).toHaveBeenCalled();
        done();
      });
    });

    it('should emit updated event after successful submission', (done) => {
      userService.editUser.and.returnValue(of(mockUserProfileResponse));
      spyOn(component.updated, 'emit');

      component.submit();

      setTimeout(() => {
        expect(component.updated.emit).toHaveBeenCalled();
        done();
      });
    });

    it('should emit closeModal event after successful submission', (done) => {
      userService.editUser.and.returnValue(of(mockUserProfileResponse));
      spyOn(component.closeModal, 'emit');

      component.submit();

      setTimeout(() => {
        expect(component.closeModal.emit).toHaveBeenCalled();
        done();
      });
    });

    it('should handle error and set error message', (done) => {
      userService.editUser.and.returnValue(throwError(() => new Error('API Error')));

      component.submit();

      setTimeout(() => {
        expect(component.isLoading()).toBeFalse();
        expect(notificationService.showError).toHaveBeenCalledWith('Erreur lors de la mise à jour.');
        done();
      });
    });

    it('should not emit events on error', (done) => {
      userService.editUser.and.returnValue(throwError(() => new Error('API Error')));
      spyOn(component.updated, 'emit');
      spyOn(component.closeModal, 'emit');

      component.submit();

      setTimeout(() => {
        expect(component.updated.emit).not.toHaveBeenCalled();
        expect(component.closeModal.emit).not.toHaveBeenCalled();
        done();
      });
    });
  });

  describe('Signal reactivity', () => {
    it('should initialize isLoading signal to false', () => {
      expect(component.isLoading()).toBeFalse();
    });
  });
});
