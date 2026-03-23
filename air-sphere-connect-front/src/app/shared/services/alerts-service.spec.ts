import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AlertsService } from './alerts-service';
import { ApiConfigService } from '../../core/services/api';
import { AddAlertPayload } from '../../core/models/user.model';

describe('AlertsService', () => {
  let service: AlertsService;
  let httpMock: HttpTestingController;
  let apiConfig: jasmine.SpyObj<ApiConfigService>;
  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
      apiUrl: mockApiUrl
    });

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AlertsService,
        { provide: ApiConfigService, useValue: apiConfigSpy }
      ]
    });

    service = TestBed.inject(AlertsService);
    httpMock = TestBed.inject(HttpTestingController);
    apiConfig = TestBed.inject(ApiConfigService) as jasmine.SpyObj<ApiConfigService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('addAlerts', () => {
    it('should add a new alert', () => {
      const payload: AddAlertPayload = { enabled: true, cityId: 1 };

      service.addAlerts(payload).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/alert/configurations`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(payload);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });

  describe('editAlerts', () => {
    it('should edit an existing alert', () => {
      const payload: AddAlertPayload = { enabled: false, cityId: 2 };
      const alertId = 5;

      service.editAlerts(payload, alertId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/alert/configurations/${alertId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(payload);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });

  describe('deleteAlerts', () => {
    it('should delete an alert', () => {
      const alertId = 3;

      service.deleteAlerts(alertId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/alert/configurations/${alertId}`);
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });
});
