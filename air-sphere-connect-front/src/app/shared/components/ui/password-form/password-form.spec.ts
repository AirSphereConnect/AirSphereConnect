import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { PasswordForm } from './password-form';
import { UserService } from '../../../services/user-service';
import { NotificationService } from '../../../services/notification-service';
import { of, throwError, Subject } from 'rxjs';
import { Router } from '@angular/router';
import { InputComponent } from '../input/input';
import { ButtonCloseModal } from '../button-close-modal/button-close-modal';
import { Button } from '../button/button';
import {UserProfileResponse} from '../../../../core/models/user.model';

describe('PasswordForm', () => {
  let component: PasswordForm;
  let fixture: ComponentFixture<PasswordForm>;
  let userService: jasmine.SpyObj<UserService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let router: jasmine.SpyObj<Router>;

  const mockUserProfile: UserProfileResponse = {
    role: 'USER',
    user: {
      id: 1,
      username: 'test',
      email: 'test@test.com',
      role: 'USER',
      address: {
        id: 1,
        street: '123 rue Test',
        city: {
          name: 'Paris',
          postalCode: '75000'
        }
      },
      favorites: [],
      alerts: []
    }
  };

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['editUser', 'fetchUserProfile', 'setUserProfile']);
    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, InputComponent, ButtonCloseModal, Button, PasswordForm],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PasswordForm);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    component.editingUserId = 1;
    component.passwordForm.get('password')?.setValue('newpassword123');
    component.passwordForm.markAsDirty();
  });

  it('should show error message if form is pristine', () => {
    component.passwordForm.markAsPristine();
    component.submit();

    expect(notificationService.showError).toHaveBeenCalledWith('Veuillez renseigner un nouveau mot de passe.');
    expect(userService.editUser).not.toHaveBeenCalled();
  });

  it('should call editUser with correct parameters on valid submission', () => {
    userService.editUser.and.returnValue(of(mockUserProfile));

    component.submit();

    expect(userService.editUser).toHaveBeenCalledWith(1, { password: 'newpassword123' });
  });

  it('should set isLoading to true during submission', () => {
    const editSubject = new Subject<UserProfileResponse>();
    userService.editUser.and.returnValue(editSubject.asObservable());

    component.submit();

    expect(component.isLoading()).toBeTrue();
    editSubject.next(mockUserProfile);
    editSubject.complete();
  });

  it('should set isLoading to false after successful submission', fakeAsync(() => {
    userService.editUser.and.returnValue(of(mockUserProfile));

    component.submit();
    tick(); // simule la fin de l'observable

    expect(component.isLoading()).toBeFalse();
  }));

  it('should handle error during submission', fakeAsync(() => {
    userService.editUser.and.returnValue(throwError(() => new Error('Network error')));

    component.submit();
    tick();

    expect(component.isLoading()).toBeFalse();
    expect(notificationService.showError).toHaveBeenCalledWith('Erreur lors de la mise à jour.');
  }));

  it('should emit updated and closeModal on successful submission', fakeAsync(() => {
    spyOn(component.updated, 'emit');
    spyOn(component.closeModal, 'emit');
    userService.editUser.and.returnValue(of(mockUserProfile));

    component.submit();
    tick();

    expect(component.updated.emit).toHaveBeenCalled();
    expect(component.closeModal.emit).toHaveBeenCalled();
  }));

  it('should handle empty response as session invalidation', fakeAsync(() => {
    userService.editUser.and.returnValue(of({} as any));

    component.submit();
    tick();

    expect(notificationService.showError).toHaveBeenCalledWith("Session invalidée côté backend, déconnexion forcée");
    expect(userService.setUserProfile).toHaveBeenCalledWith(null);
    expect(userService.fetchUserProfile).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/home']);
  }));
});
