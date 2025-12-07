import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError, BehaviorSubject } from 'rxjs';

import { UserDashboard } from './user';
import { UserService } from '../../../../shared/services/user-service';

describe('UserDashboard', () => {
  let component: UserDashboard;
  let fixture: ComponentFixture<UserDashboard>;
  let userService: jasmine.SpyObj<UserService>;
  let router: Router;
  let userProfileSubject: BehaviorSubject<any>;

  const mockUser = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    role: 'USER',
    address: {
      id: 1,
      street: '123 Rue de la Paix',
      city: { id: 1, name: 'Paris' }
    },
    favorites: [],
    alerts: []
  };

  beforeEach(async () => {
    userProfileSubject = new BehaviorSubject<any>({ user: mockUser });

    const userServiceSpy = jasmine.createSpyObj('UserService', ['deleteUser', 'fetchUserProfile'], {
      userProfile$: userProfileSubject.asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [UserDashboard],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: UserService, useValue: userServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDashboard);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize user from userProfile$ subscription', () => {
      expect(component.user as any).toEqual(mockUser);
    });

    it('should initialize all modal signals to false', () => {
      expect(component.isUserModalOpen()).toBeFalse();
      expect(component.isEmailModalOpen()).toBeFalse();
      expect(component.isPasswordModalOpen()).toBeFalse();
      expect(component.isAddressModalOpen()).toBeFalse();
      expect(component.isWarningOpen()).toBeFalse();
    });

    it('should initialize warningMessage to null', () => {
      expect(component.warningMessage()).toBeNull();
    });

    it('should initialize editing IDs to null', () => {
      expect(component.editingUserId).toBeNull();
      expect(component.userToDeleteId).toBeNull();
    });

    it('should initialize initial data to null', () => {
      expect(component.initialUserData).toBeNull();
      expect(component.initialEmailData).toBeNull();
      expect(component.initialPasswordData).toBeNull();
      expect(component.initialAddressData).toBeNull();
    });

    it('should update user when userProfile$ emits', () => {
      const newUser = { id: 2, username: 'newuser', email: 'new@example.com', role: 'USER', address: null, favorites: [], alerts: [] };
      userProfileSubject.next({ user: newUser });
      expect(component.user as any).toEqual(newUser);
    });
  });

  describe('editUser', () => {
    it('should set editingUserId', () => {
      component.editUser();
      expect(component.editingUserId).toBe(1);
    });

    it('should set initialUserData', () => {
      component.editUser();
      expect(component.initialUserData).toEqual(mockUser);
    });

    it('should open user modal', () => {
      component.editUser();
      expect(component.isUserModalOpen()).toBeTrue();
    });

    it('should not open modal if user is null', () => {
      component.user = null;
      component.editUser();
      expect(component.isUserModalOpen()).toBeFalse();
    });
  });

  describe('editEmail', () => {
    it('should set editingUserId', () => {
      component.editEmail();
      expect(component.editingUserId).toBe(1);
    });

    it('should set initialEmailData', () => {
      component.editEmail();
      expect(component.initialEmailData).toEqual(mockUser);
    });

    it('should open email modal', () => {
      component.editEmail();
      expect(component.isEmailModalOpen()).toBeTrue();
    });

    it('should not open modal if user is null', () => {
      component.user = null;
      component.editEmail();
      expect(component.isEmailModalOpen()).toBeFalse();
    });
  });

  describe('editPassword', () => {
    it('should set editingUserId', () => {
      component.editPassword();
      expect(component.editingUserId).toBe(1);
    });

    it('should set initialPasswordData', () => {
      component.editPassword();
      expect(component.initialPasswordData).toEqual(mockUser);
    });

    it('should open password modal', () => {
      component.editPassword();
      expect(component.isPasswordModalOpen()).toBeTrue();
    });

    it('should not open modal if user is null', () => {
      component.user = null;
      component.editPassword();
      expect(component.isPasswordModalOpen()).toBeFalse();
    });
  });

  describe('editAddress', () => {
    it('should set editingUserId', () => {
      component.editAddress();
      expect(component.editingUserId).toBe(1);
    });

    it('should set initialAddressData', () => {
      component.editAddress();
      expect(component.initialAddressData).toEqual(mockUser.address);
    });

    it('should open address modal', () => {
      component.editAddress();
      expect(component.isAddressModalOpen()).toBeTrue();
    });

    it('should not open modal if user is null', () => {
      component.user = null;
      component.editAddress();
      expect(component.isAddressModalOpen()).toBeFalse();
    });

    it('should not open modal if address is null', () => {
      component.user = { ...mockUser, address: null } as any;
      component.editAddress();
      expect(component.isAddressModalOpen()).toBeFalse();
    });
  });

  describe('Modal close methods', () => {
    it('should close user modal', () => {
      component.isUserModalOpen.set(true);
      component.onUserModalClose();
      expect(component.isUserModalOpen()).toBeFalse();
    });

    it('should close email modal', () => {
      component.isEmailModalOpen.set(true);
      component.onEmailModalClose();
      expect(component.isEmailModalOpen()).toBeFalse();
    });

    it('should close password modal', () => {
      component.isPasswordModalOpen.set(true);
      component.onPasswordModalClose();
      expect(component.isPasswordModalOpen()).toBeFalse();
    });

    it('should close address modal', () => {
      component.isAddressModalOpen.set(true);
      component.onAddressModalClose();
      expect(component.isAddressModalOpen()).toBeFalse();
    });
  });

  describe('deleteUser', () => {
    it('should set userToDeleteId', () => {
      component.deleteUser(1);
      expect(component.userToDeleteId).toBe(1);
    });

    it('should set warning message', () => {
      component.deleteUser(1);
      expect(component.warningMessage()).toBe('Êtes-vous sûr de vouloir supprimer votre compte ?');
    });

    it('should open warning dialog', () => {
      component.deleteUser(1);
      expect(component.isWarningOpen()).toBeTrue();
    });
  });

  describe('confirmDelete', () => {
    beforeEach(() => {
      component.userToDeleteId = 1;
    });

    it('should not call deleteUser if userToDeleteId is null', () => {
      component.userToDeleteId = null;
      component.confirmDelete();
      expect(userService.deleteUser).not.toHaveBeenCalled();
    });

    it('should call deleteUser with correct id', () => {
      userService.deleteUser.and.returnValue(of(undefined));
      component.confirmDelete();
      expect(userService.deleteUser).toHaveBeenCalledWith(1);
    });

    describe('on success', () => {
      beforeEach(() => {
        userService.deleteUser.and.returnValue(of(undefined));
      });

      it('should navigate to /home', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(router.navigate).toHaveBeenCalledWith(['/home']);
          done();
        });
      });

      it('should reset userToDeleteId', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(component.userToDeleteId).toBeNull();
          done();
        });
      });

      it('should close warning dialog', (done) => {
        component.isWarningOpen.set(true);
        component.confirmDelete();
        setTimeout(() => {
          expect(component.isWarningOpen()).toBeFalse();
          done();
        });
      });

      it('should clear warning message', (done) => {
        component.warningMessage.set('Test message');
        component.confirmDelete();
        setTimeout(() => {
          expect(component.warningMessage()).toBeNull();
          done();
        });
      });
    });

    describe('on error', () => {
      beforeEach(() => {
        userService.deleteUser.and.returnValue(throwError(() => new Error('Delete failed')));
      });

      it('should close warning dialog', (done) => {
        component.isWarningOpen.set(true);
        component.confirmDelete();
        setTimeout(() => {
          expect(component.isWarningOpen()).toBeFalse();
          done();
        });
      });

      it('should reset userToDeleteId', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(component.userToDeleteId).toBeNull();
          done();
        });
      });

      it('should clear warning message', (done) => {
        component.warningMessage.set('Test message');
        component.confirmDelete();
        setTimeout(() => {
          expect(component.warningMessage()).toBeNull();
          done();
        });
      });

      it('should not navigate on error', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(router.navigate).not.toHaveBeenCalled();
          done();
        });
      });
    });
  });
});
