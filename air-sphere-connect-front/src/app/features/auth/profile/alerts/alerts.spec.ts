import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

import { Alerts } from './alerts';
import { AlertsService } from '../../../../shared/services/alerts-service';
import { UserService } from '../../../../shared/services/user-service';

describe('Alerts', () => {
  let component: Alerts;
  let fixture: ComponentFixture<Alerts>;
  let alertsService: jasmine.SpyObj<AlertsService>;
  let userService: jasmine.SpyObj<UserService>;

  const mockUser = {
    id: 1,
    username: 'testuser',
    alerts: [
      { id: 1, cityId: 1, cityName: 'Paris', enabled: true },
      { id: 2, cityId: 2, cityName: 'Lyon', enabled: false }
    ]
  };

  beforeEach(async () => {
    const alertsServiceSpy = jasmine.createSpyObj('AlertsService', ['deleteAlerts']);
    const userServiceSpy = jasmine.createSpyObj('UserService', ['fetchUserProfile']);

    await TestBed.configureTestingModule({
      imports: [Alerts, HttpClientTestingModule],
      providers: [
        { provide: AlertsService, useValue: alertsServiceSpy },
        { provide: UserService, useValue: userServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Alerts);
    component = fixture.componentInstance;
    alertsService = TestBed.inject(AlertsService) as jasmine.SpyObj<AlertsService>;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    component.user = mockUser as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize editingAlertsId as null', () => {
      expect(component.editingAlertsId).toBeNull();
    });

    it('should initialize initialAlertData as null', () => {
      expect(component.initialAlertData).toBeNull();
    });

    it('should initialize alertToDeleteId as null', () => {
      expect(component.alertToDeleteId).toBeNull();
    });

    it('should initialize isModalOpen to false', () => {
      expect(component.isModalOpen()).toBeFalse();
    });

    it('should initialize isWarningOpen to false', () => {
      expect(component.isWarningOpen()).toBeFalse();
    });

    it('should initialize warningMessage to null', () => {
      expect(component.warningMessage()).toBeNull();
    });
  });

  describe('addAlerts', () => {
    it('should reset editingAlertsId to null', () => {
      component.editingAlertsId = 1;
      component.addAlerts();
      expect(component.editingAlertsId).toBeNull();
    });

    it('should reset initialAlertData to null', () => {
      component.initialAlertData = { id: 1 };
      component.addAlerts();
      expect(component.initialAlertData).toBeNull();
    });

    it('should open modal', () => {
      component.addAlerts();
      expect(component.isModalOpen()).toBeTrue();
    });
  });

  describe('editAlerts', () => {
    it('should set editingAlertsId when alert exists', () => {
      component.editAlerts(1);
      expect(component.editingAlertsId).toBe(1);
    });

    it('should set initialAlertData with alert data', () => {
      component.editAlerts(1);
      expect(component.initialAlertData).toEqual(mockUser.alerts[0]);
    });

    it('should open modal when alert exists', () => {
      component.editAlerts(1);
      expect(component.isModalOpen()).toBeTrue();
    });

    it('should call fetchUserProfile when alert exists', () => {
      component.editAlerts(1);
      expect(userService.fetchUserProfile).toHaveBeenCalled();
    });

    it('should not open modal when alert does not exist', () => {
      component.editAlerts(999);
      expect(component.isModalOpen()).toBeFalse();
    });

    it('should not set editingAlertsId when alert does not exist', () => {
      component.editAlerts(999);
      expect(component.editingAlertsId).toBeNull();
    });

    it('should handle null user', () => {
      component.user = null;
      component.editAlerts(1);
      expect(component.isModalOpen()).toBeFalse();
    });
  });

  describe('deleteAlerts', () => {
    it('should set alertToDeleteId', () => {
      component.deleteAlerts(1);
      expect(component.alertToDeleteId).toBe(1);
    });

    it('should set warning message', () => {
      component.deleteAlerts(1);
      expect(component.warningMessage()).toBe('Êtes-vous sûr de vouloir supprimer cette alerte ?');
    });

    it('should open warning dialog', () => {
      component.deleteAlerts(1);
      expect(component.isWarningOpen()).toBeTrue();
    });
  });

  describe('confirmDelete', () => {
    beforeEach(() => {
      component.alertToDeleteId = 1;
    });

    it('should not call deleteAlerts if alertToDeleteId is null', () => {
      component.alertToDeleteId = null;
      component.confirmDelete();
      expect(alertsService.deleteAlerts).not.toHaveBeenCalled();
    });

    it('should call deleteAlerts with correct id', () => {
      alertsService.deleteAlerts.and.returnValue(of(undefined));
      component.confirmDelete();
      expect(alertsService.deleteAlerts).toHaveBeenCalledWith(1);
    });

    describe('on success', () => {
      beforeEach(() => {
        alertsService.deleteAlerts.and.returnValue(of(undefined));
      });

      it('should fetch user profile', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(userService.fetchUserProfile).toHaveBeenCalled();
          done();
        });
      });

      it('should reset alertToDeleteId', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(component.alertToDeleteId).toBeNull();
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
        alertsService.deleteAlerts.and.returnValue(throwError(() => new Error('Delete failed')));
      });

      it('should close warning dialog', (done) => {
        component.isWarningOpen.set(true);
        component.confirmDelete();
        setTimeout(() => {
          expect(component.isWarningOpen()).toBeFalse();
          done();
        });
      });

      it('should reset alertToDeleteId', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(component.alertToDeleteId).toBeNull();
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

      it('should not fetch user profile on error', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(userService.fetchUserProfile).not.toHaveBeenCalled();
          done();
        });
      });
    });
  });

  describe('onModalClose', () => {
    it('should close modal', () => {
      component.isModalOpen.set(true);
      component.onModalClose();
      expect(component.isModalOpen()).toBeFalse();
    });
  });
});
