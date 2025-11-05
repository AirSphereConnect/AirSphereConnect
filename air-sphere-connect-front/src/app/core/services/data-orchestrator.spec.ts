import { TestBed } from '@angular/core/testing';

import { DataOrchestratorService } from './data-orchestrator';

describe('DataOrchestratorService', () => {
  let service: DataOrchestratorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataOrchestratorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
