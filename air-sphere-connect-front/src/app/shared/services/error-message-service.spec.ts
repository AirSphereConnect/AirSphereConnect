import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NotificationService } from './notification-service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NotificationService]
    });
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('notification signal', () => {
    it('should initialize notification as null', () => {
      expect(service.notification()).toBeNull();
    });
  });

  describe('showError', () => {
    it('should set error notification', () => {
      const testMessage = 'Test error message';
      service.showError(testMessage);
      expect(service.notification()).toEqual({ type: 'error', message: testMessage });
    });

    it('should clear notification after default duration (5000ms)', fakeAsync(() => {
      const testMessage = 'Test error message';
      service.showError(testMessage);
      expect(service.notification()).toEqual({ type: 'error', message: testMessage });

      tick(5000);
      expect(service.notification()).toBeNull();
    }));

    it('should clear notification after custom duration', fakeAsync(() => {
      const testMessage = 'Test error message';
      const customDuration = 3000;
      service.showError(testMessage, customDuration);
      expect(service.notification()).toEqual({ type: 'error', message: testMessage });

      tick(2999);
      expect(service.notification()).toEqual({ type: 'error', message: testMessage });

      tick(1);
      expect(service.notification()).toBeNull();
    }));
  });

  describe('showSuccess', () => {
    it('should set success notification', () => {
      const testMessage = 'Test success message';
      service.showSuccess(testMessage);
      expect(service.notification()).toEqual({ type: 'success', message: testMessage });
    });

    it('should clear notification after default duration (3000ms)', fakeAsync(() => {
      const testMessage = 'Test success message';
      service.showSuccess(testMessage);
      expect(service.notification()).toEqual({ type: 'success', message: testMessage });

      tick(3000);
      expect(service.notification()).toBeNull();
    }));

    it('should clear notification after custom duration', fakeAsync(() => {
      const testMessage = 'Test success message';
      const customDuration = 5000;
      service.showSuccess(testMessage, customDuration);
      expect(service.notification()).toEqual({ type: 'success', message: testMessage });

      tick(4999);
      expect(service.notification()).toEqual({ type: 'success', message: testMessage });

      tick(1);
      expect(service.notification()).toBeNull();
    }));
  });

  describe('show', () => {
    it('should set notification with specified type', () => {
      const testMessage = 'Test message';
      service.show(testMessage, 'error');
      expect(service.notification()).toEqual({ type: 'error', message: testMessage });
    });

    it('should handle immediate duration (0ms)', fakeAsync(() => {
      const testMessage = 'Test message';
      service.show(testMessage, 'error', 0);
      expect(service.notification()).toEqual({ type: 'error', message: testMessage });

      tick(0);
      expect(service.notification()).toBeNull();
    }));

    it('should override previous notification', fakeAsync(() => {
      service.show('First message', 'error');
      expect(service.notification()).toEqual({ type: 'error', message: 'First message' });

      service.show('Second message', 'success');
      expect(service.notification()).toEqual({ type: 'success', message: 'Second message' });

      tick(5000);
      expect(service.notification()).toBeNull();
    }));

    it('should handle multiple consecutive notifications', fakeAsync(() => {
      service.show('Message 1', 'error', 1000);
      expect(service.notification()).toEqual({ type: 'error', message: 'Message 1' });

      tick(500);
      service.show('Message 2', 'success', 1000);
      expect(service.notification()).toEqual({ type: 'success', message: 'Message 2' });

      tick(1000);
      expect(service.notification()).toBeNull();
    }));
  });

  describe('clear', () => {
    it('should clear notification immediately', () => {
      service.showError('Test error');
      expect(service.notification()).toEqual({ type: 'error', message: 'Test error' });

      service.clear();
      expect(service.notification()).toBeNull();
    });

    it('should handle clearing when notification is already null', () => {
      expect(service.notification()).toBeNull();
      service.clear();
      expect(service.notification()).toBeNull();
    });
  });
});
